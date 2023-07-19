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
import com.xquare.v1servicepoint.point.spi.NotificationSpi
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
    private val notificationSpi: NotificationSpi,
) : PointHistoryApi {

    companion object {
        const val POINT_REASON = "다벌점 교육 완료"
        val PENALTY_LEVEL_LIST = listOf(15, 20, 25, 35, 45)
    }

    override suspend fun saveUserPoint(userId: UUID, givePointUserRequest: DomainGivePointUserRequest) {
        val point: Point = queryPointSpi.findByPointId(givePointUserRequest.pointId)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)

        val pointStatus: PointStatus = queryPointStatusSpi.findByUserId(userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        when (point.type) {
            true -> {
                val addGoodPoint = pointStatus.addGoodPoint(point.point)
                commandPointStatusSpi.applyPointStatusChanges(addGoodPoint)

                sendNotification(userId, true, point.reason, point.point)
            }

            false -> {
                val addBadPoint = pointStatus.addBadPoint(point.point)
                if (!addBadPoint.isPenaltyRequired && addBadPoint.badPoint >= PENALTY_LEVEL_LIST[addBadPoint.penaltyLevel]) {
                    val penaltyLevel = addBadPoint.penaltyEducationStart().penaltyLevelUp()
                    commandPointStatusSpi.applyPointStatusChanges(penaltyLevel)

                    sendNotification(userId, false, point.reason, point.point)
                    sendPenaltyNotification(userId, addBadPoint.badPoint, penaltyLevel.penaltyLevel)
                } else {
                    commandPointStatusSpi.applyPointStatusChanges(addBadPoint)

                    sendNotification(userId, false, point.reason, point.point)
                }
            }
        }
        commandPointHistorySpi.saveUserPointHistory(userId, point.id)
    }

    private suspend fun sendNotification(userId: UUID, isGoodPoint: Boolean, reason: String, point: Int) {
        val notificationMessage = convertPostPosition(reason) + " 인해 ${point}점을 받았습니다."
        val topic = if (isGoodPoint) "ALL_GOOD_POINT" else "ALL_BAD_POINT"
        val threadId = "ALL_POINT"

        notificationSpi.sendNotification(userId, topic, notificationMessage, threadId)
    }

    private suspend fun sendPenaltyNotification(userId: UUID, point: Int, penaltyLevel: Int) {
        val penaltyNotificationMessage = "${point}점이 되어 ${penaltyLevel}차 봉사 대상이 되었어요."
        val topic = "ALL_PENALTY_LEVEL"
        val threadId = "ALL_POINT"

        notificationSpi.sendNotification(userId, topic, penaltyNotificationMessage, threadId)
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

    private fun convertPostPosition(reason: String): String {
        val lastSpell = reason.last()
        return if ((lastSpell.code - 0xAC00) % 28 > 0 && (lastSpell.code - 0xAC00) % 28 != 8) {
            reason + "으로"
        } else {
            reason + "로"
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
