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
import com.xquare.v1servicepoint.point.exception.UserNotFoundException
import com.xquare.v1servicepoint.point.spi.ExcelSpi
import com.xquare.v1servicepoint.point.spi.point.QueryPointSpi
import com.xquare.v1servicepoint.point.spi.pointhistory.CommandPointHistorySpi
import com.xquare.v1servicepoint.point.spi.pointhistory.QueryPointHistorySpi
import com.xquare.v1servicepoint.point.spi.pointstatus.CommandPointStatusSpi
import com.xquare.v1servicepoint.point.spi.pointstatus.QueryPointStatusSpi
import java.nio.charset.Charset
import java.time.LocalDate
import java.util.UUID

@UseCase
class PointHistoryApiImpl(
    private val queryPointSpi: QueryPointSpi,
    private val commandPointStatusSpi: CommandPointStatusSpi,
    private val queryPointStatusSpi: QueryPointStatusSpi,
    private val queryPointHistorySpi: QueryPointHistorySpi,
    private val commandPointHistorySpi: CommandPointHistorySpi,
    private val excelSpi: ExcelSpi,
) : PointHistoryApi {

    companion object {
        const val POINT_REASON = "다벌점 교육 완료"
        val PENALTY_LEVEL_LIST = listOf(15, 20, 25, 35, 45)
    }

    override suspend fun saveUserPoint(userId: UUID, givePointUserRequest: DomainGivePointUserRequest) {
        val getPointByPointId: Point = queryPointSpi.findByPointId(givePointUserRequest.pointId)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)

        val pointStatus: PointStatus = queryPointStatusSpi.findByUserId(userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)
        when (getPointByPointId.type) {
            true -> {
                val addGoodPoint = pointStatus.addGoodPoint(getPointByPointId.point)
                commandPointStatusSpi.applyPointStatusChanges(addGoodPoint)
            }

            false -> {
                val addBadPoint = pointStatus.addBadPoint(getPointByPointId.point)
                if (!addBadPoint.isPenaltyRequired && addBadPoint.badPoint >= PENALTY_LEVEL_LIST[addBadPoint.penaltyLevel]) {
                    val penaltyLevel = addBadPoint.penaltyEducationStart().penaltyLevelUp()
                    commandPointStatusSpi.applyPointStatusChanges(penaltyLevel)
                } else {
                    commandPointStatusSpi.applyPointStatusChanges(addBadPoint)
                }
            }
        }
        commandPointHistorySpi.saveUserPointHistory(userId, getPointByPointId.id)
    }

    override suspend fun deleteUserPoint(studentId: UUID, historyId: UUID) {
        val pointHistory = queryPointHistorySpi.findByIdAndStudentId(historyId, studentId)
            ?: throw PointHistoryNotFoundException(PointHistoryNotFoundException.POINT_HISTORY_NOT_FOUND)

        val getPointByPointId = queryPointSpi.findByPointId(pointHistory.pointId)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)

        val pointStatus = queryPointStatusSpi.findByUserId(pointHistory.userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        when (getPointByPointId.type) {
            true -> {
                val minusGoodPoint = pointStatus.minusGoodPoint(getPointByPointId.point)
                commandPointStatusSpi.applyPointStatusChanges(minusGoodPoint)
            }

            false -> {
                val minusBadPoint = pointStatus.minusBadPoint(getPointByPointId.point)
                commandPointStatusSpi.applyPointStatusChanges(minusBadPoint)
            }
        }

        commandPointHistorySpi.deleteByIdAndUserId(pointHistory)
    }

    override suspend fun queryUserPointHistory(userId: UUID, type: String): PointHistoryListResponse {
        val pointHistory = queryPointHistorySpi.findAllByUserIdAndType(userId, convertType(type))
        return PointHistoryListResponse(pointHistory)
    }

    override suspend fun queryUserPointHistoryForStudent(userId: UUID, type: String): PointHistoryListStudentResponse {
        val getUserPointStatus = queryPointStatusSpi.findByUserId(userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        val pointHistory = queryPointHistorySpi.findAllByUserIdAndType(getUserPointStatus.userId, convertType(type))

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

    override suspend fun queryUserPointHistoryExcel(): ExportUserPointStatusResponse {
        val fileName = String(
            bytes = "상벌점 부여내역 ${LocalDate.now()}.xlsx".toByteArray(charset("UTF-8")),
            charset = Charset.forName("ISO-8859-1")
        )

        return ExportUserPointStatusResponse(
            file = excelSpi.writeUserPointHistoryExcelFile(),
            fileName = fileName,
        )
    }
}
