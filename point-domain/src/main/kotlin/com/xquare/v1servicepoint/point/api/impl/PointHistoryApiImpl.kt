package com.xquare.v1servicepoint.point.api.impl

import com.xquare.v1servicepoint.annotation.UseCase
import com.xquare.v1servicepoint.point.Point
import com.xquare.v1servicepoint.point.PointStatus
import com.xquare.v1servicepoint.point.api.PointHistoryApi
import com.xquare.v1servicepoint.point.api.dto.request.DomainGivePointUserRequest
import com.xquare.v1servicepoint.point.api.dto.response.ExportUserPointStatusResponse
import com.xquare.v1servicepoint.point.api.dto.response.PointHistoryListResponse
import com.xquare.v1servicepoint.point.api.dto.response.PointHistoryListStudentResponse
import com.xquare.v1servicepoint.point.exception.PointHistoryNotFoundException
import com.xquare.v1servicepoint.point.exception.PointNotFoundException
import com.xquare.v1servicepoint.point.exception.UserExistException
import com.xquare.v1servicepoint.point.exception.UserNotFoundException
import com.xquare.v1servicepoint.point.exception.UserPenaltyExistException
import com.xquare.v1servicepoint.point.spi.ExcelSpi
import com.xquare.v1servicepoint.point.spi.PointHistorySpi
import com.xquare.v1servicepoint.point.spi.PointSpi
import com.xquare.v1servicepoint.point.spi.PointStatusSpi
import java.nio.charset.Charset
import java.time.LocalDate
import java.util.UUID

@UseCase
class PointHistoryApiImpl(
    private val pointHistorySpi: PointHistorySpi,
    private val pointSpi: PointSpi,
    private val pointStatusSpi: PointStatusSpi,
    private val excelSpi: ExcelSpi,
) : PointHistoryApi {

    override suspend fun saveUserPoint(userId: UUID, givePointUserRequest: DomainGivePointUserRequest) {
        val getPointByPointId: Point = pointSpi.findByPointId(givePointUserRequest.pointId)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)

        val pointStatus: PointStatus = pointStatusSpi.findByUserId(userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        when(getPointByPointId.type) {
            true -> {
                val addGoodPoint = pointStatus.addGoodPoint(getPointByPointId.point)
                pointStatusSpi.applyPointStatusChanges(addGoodPoint)
            }

            false -> {
                val addBadPoint = pointStatus.addBadPoint(getPointByPointId.point)

                val penaltyLevel = listOf(15, 20, 25, 30, 35, 40, 45)

                if (addBadPoint.badPoint >= penaltyLevel[addBadPoint.penaltyLevel - 1]) {
                    val penaltyLevel = addBadPoint.penaltyEducationStart()
                    pointStatusSpi.applyPointStatusChanges(penaltyLevel)
                } else {
                    pointStatusSpi.applyPointStatusChanges(addBadPoint)
                }
            }
        }
        pointHistorySpi.saveUserPoint(userId, getPointByPointId.id)
    }

    override suspend fun saveUserPenaltyEducationComplete(userId: UUID) {
        val pointStatus = pointStatusSpi.findByUserId(userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        // 벌점 봉사 완료시 벌점봉사완료 라는 이름의 point rule 을 직접 찾아 넣어준다.
        val badPoint = pointSpi.findByReasonAndType("벌점 봉사 완료", false)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)

        val goodPoint = pointSpi.findByReasonAndType("벌점 봉사 완료", true)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)

        when (pointStatus.isPenaltyRequired) {
            false -> throw UserPenaltyExistException(UserPenaltyExistException.USER_PENALTY_EXIST)
            true -> {
                val penaltyEducationComplete = pointStatus.penaltyEducationCompleteAndMinusGoodPoint().penaltyEducationCompleteAndMinusBadPoint().penaltyEducationComplete().penaltyLevelUp()
                val penaltyLevel = listOf(15, 20, 25, 35, 45)

                if (penaltyEducationComplete.badPoint >= penaltyLevel[penaltyEducationComplete.penaltyLevel - 1]) {
                    val penaltyLevel = penaltyEducationComplete.penaltyEducationStart()
                    pointStatusSpi.applyPointStatusChanges(penaltyLevel)
                }
                else {
                    val penaltyLevel = penaltyEducationComplete.penaltyEducationComplete()
                    pointStatusSpi.applyPointStatusChanges(penaltyLevel)
                }
            }
        }

        pointHistorySpi.saveUserPoint(pointStatus.userId, badPoint.id)
        pointHistorySpi.saveUserPoint(pointStatus.userId, goodPoint.id)
    }

    override suspend fun deleteUserPoint(studentId: UUID, historyId: UUID) {
        val pointHistory = pointHistorySpi.findByIdAndStudentId(historyId, studentId)
            ?: throw PointHistoryNotFoundException(PointHistoryNotFoundException.POINT_HISTORY_NOT_FOUND)

        val getPointByPointId = pointSpi.findByPointId(pointHistory.pointId)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)

        val pointStatus = pointStatusSpi.findByUserId(pointHistory.userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        when (getPointByPointId.type) {
            true -> {
                val minusGoodPoint = pointStatus.minusGoodPoint(getPointByPointId.point)
                pointStatusSpi.applyPointStatusChanges(minusGoodPoint)
            }

            false -> {
                val minusBadPoint = pointStatus.minusBadPoint(getPointByPointId.point)
                pointStatusSpi.applyPointStatusChanges(minusBadPoint)
            }
        }

        pointHistorySpi.deleteByIdAndUserId(pointHistory)
    }

    override suspend fun queryUserPointHistory(userId: UUID, type: String): PointHistoryListResponse {
        val pointHistory = pointHistorySpi.findAllByUserIdAndType(userId, convertType(type))
        return PointHistoryListResponse(pointHistory)
    }

    override suspend fun queryUserPointHistoryForStudent(userId: UUID, type: String): PointHistoryListStudentResponse {
        val getUserPointStatus = pointStatusSpi.findByUserId(userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        val pointHistory = pointHistorySpi.findAllByUserIdAndType(getUserPointStatus.userId, convertType(type))

        return PointHistoryListStudentResponse(
            goodPoint = getUserPointStatus.goodPoint,
            badPoint = getUserPointStatus.badPoint,
            pointHistories = pointHistory,
        )
    }

    private fun convertType(type: String): Boolean? {
        return when (type) {
            "GOODPOINT" -> true
            "BADPOINT" -> false
            else -> null
        }
    }

    override suspend fun savePointStatus(userId: UUID) {
        pointStatusSpi.findByUserId(userId)?.run {
            throw UserExistException(UserExistException.USER_ID_EXIST)
        }

        val pointStatus = PointStatus(
            userId = userId,
            goodPoint = 0,
            badPoint = 0,
            penaltyLevel = 1,
            isPenaltyRequired = false,
        )
        pointStatusSpi.savePointStatus(pointStatus)
    }

    override suspend fun queryUserPointHistoryExcel(): ExportUserPointStatusResponse {
        val fileName =
            String("상벌점 부여내역 ${LocalDate.now()}.xlsx".toByteArray(charset("UTF-8")), Charset.forName("ISO-8859-1"))

        return ExportUserPointStatusResponse(
            file = excelSpi.writeUserPointHistoryExcelFile(),
            fileName = fileName,
        )
    }
}
