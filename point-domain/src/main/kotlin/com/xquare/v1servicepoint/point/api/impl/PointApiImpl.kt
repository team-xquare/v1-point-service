package com.xquare.v1servicepoint.point.api.impl

import com.xquare.v1servicepoint.annotation.UseCase
import com.xquare.v1servicepoint.point.api.PointApi
import com.xquare.v1servicepoint.point.api.dto.response.PointStatusResponse
import com.xquare.v1servicepoint.point.exception.PointNotFoundException
import com.xquare.v1servicepoint.point.exception.UserNotFoundException
import com.xquare.v1servicepoint.point.spi.PointHistorySpi
import com.xquare.v1servicepoint.point.spi.PointSpi
import com.xquare.v1servicepoint.point.spi.PointStatusSpi
import java.util.UUID

@UseCase
class PointApiImpl(
    private val pointStatusSpi: PointStatusSpi,
    private val pointSpi: PointSpi,
    private val pointHistorySpi: PointHistorySpi
) : PointApi {

    override suspend fun queryPointStatus(userId: UUID): PointStatusResponse {
        val getUserPointStatus = pointStatusSpi.findByUserId(userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        return PointStatusResponse(
            goodPoint = getUserPointStatus.goodPoint,
            badPoint = getUserPointStatus.badPoint,
        )
    }

    override suspend fun deletePointRole(pointId: UUID) {
        val point = pointSpi.findByPointId(pointId)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)
        val userPointHistory = pointHistorySpi.findAllByPointId(point.id)

        val pointStatus = pointStatusSpi.findByUserId(userPointHistory.first().userId)

        userPointHistory.forEach {
            pointHistorySpi.deleteByIdAndUserId(it)

            when (point.type) {
                true -> pointStatus?.minusGoodPoint(point.point)
                false -> pointStatus?.minusBadPoint(point.point)
            }
        }
        pointSpi.deletePointRole(point.id)
    }
}
