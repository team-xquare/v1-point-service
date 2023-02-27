package com.xquare.v1servicepoint.point.api.impl

import com.xquare.v1servicepoint.annotation.UseCase
import com.xquare.v1servicepoint.point.PointStatus
import com.xquare.v1servicepoint.point.api.PointHistoryApi
import com.xquare.v1servicepoint.point.api.dto.request.DomainGivePointUserRequest
import com.xquare.v1servicepoint.point.api.dto.response.PointHistoryListResponse
import com.xquare.v1servicepoint.point.exception.PointHistoryNotFoundException
import com.xquare.v1servicepoint.point.exception.PointNotFoundException
import com.xquare.v1servicepoint.point.exception.UserExistException
import com.xquare.v1servicepoint.point.exception.UserNotFoundException
import com.xquare.v1servicepoint.point.spi.PointHistorySpi
import com.xquare.v1servicepoint.point.spi.PointSpi
import com.xquare.v1servicepoint.point.spi.PointStatusSpi
import java.util.UUID

@UseCase
class PointHistoryApiImpl(
    private val pointHistorySpi: PointHistorySpi,
    private val pointSpi: PointSpi,
    private val pointStatusSpi: PointStatusSpi,
) : PointHistoryApi {

    override suspend fun saveUserPoint(userId: UUID, givePointUserRequest: DomainGivePointUserRequest) {
        val getPointByPointId = pointSpi.findByPointId(givePointUserRequest.pointId)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)

        val pointStatus = pointStatusSpi.findByUserId(userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        when (getPointByPointId.type) {
            true -> pointStatus.addGoodPoint(getPointByPointId.point)
            false -> pointStatus.addBadPoint(getPointByPointId.point)
        }

        pointHistorySpi.saveUserPoint(userId, getPointByPointId.id)
    }

    override suspend fun deleteUserPoint(studentId: UUID, historyId: UUID) {
        val pointHistory = pointHistorySpi.findByIdAndStudentId(historyId, studentId)
            ?: throw PointHistoryNotFoundException(PointHistoryNotFoundException.POINT_HISTORY_NOT_FOUND)

        val getPointByPointId = pointSpi.findByPointId(pointHistory.pointId)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)

        val pointStatus = pointStatusSpi.findByUserId(pointHistory.userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        when (getPointByPointId.type) {
            true -> pointStatus.minusGoodPoint(getPointByPointId.point)
            false -> pointStatus.minusBadPoint(getPointByPointId.point)
        }

        pointHistorySpi.deleteByIdAndUserId(pointHistory)
    }

    override suspend fun queryUserPointHistory(userId: UUID, type: Boolean): PointHistoryListResponse {
        val pointHistoryList = pointHistorySpi.findAllByUserIdAndType(userId, type)
        return PointHistoryListResponse(pointHistoryList)
    }

    override suspend fun savePointStatus(userId: UUID) {
        pointStatusSpi.findByUserId(userId)
            ?: throw UserExistException(UserExistException.USER_ID_EXIST)

        val pointStatus = PointStatus(
            userId = userId,
            goodPoint = 0,
            badPoint = 0,
            penaltyLevel = 0,
            isPenaltyRequired = false,
        )
        pointStatusSpi.savePointStatus(pointStatus)
    }
}
