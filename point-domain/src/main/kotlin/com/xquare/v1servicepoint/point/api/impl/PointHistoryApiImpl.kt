package com.xquare.v1servicepoint.point.api.impl

import com.xquare.v1servicepoint.annotation.UseCase
import com.xquare.v1servicepoint.point.api.PointHistoryApi
import com.xquare.v1servicepoint.point.api.dto.request.DomainGivePointUserRequest
import com.xquare.v1servicepoint.point.exception.PointNotFoundException
import com.xquare.v1servicepoint.point.exception.UserNotFoundException
import com.xquare.v1servicepoint.point.spi.PointHistorySpi
import com.xquare.v1servicepoint.point.spi.PointStatusSpi
import java.util.UUID

@UseCase
class PointHistoryApiImpl(
    private val pointHistorySpi: PointHistorySpi,
    private val pointStatusSpi: PointStatusSpi,
) : PointHistoryApi {

    override suspend fun saveUserPoint(userId: UUID, givePointUserRequest: DomainGivePointUserRequest) {
        val getPointByPointId = pointHistorySpi.findByPointId(givePointUserRequest.pointId)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)

        val pointStatus = pointStatusSpi.findByUserId(userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        when (getPointByPointId.type) {
            true -> pointStatus.addGoodPoint(getPointByPointId.point)
            false -> pointStatus.addBadPoint(getPointByPointId.point)
        }

        pointHistorySpi.saveUserPoint(userId, getPointByPointId.id)
    }
}
