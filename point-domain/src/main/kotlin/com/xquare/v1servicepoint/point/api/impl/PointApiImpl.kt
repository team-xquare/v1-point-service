package com.xquare.v1servicepoint.point.api.impl

import com.xquare.v1servicepoint.annotation.UseCase
import com.xquare.v1servicepoint.point.api.PointApi
import com.xquare.v1servicepoint.point.api.dto.request.DomainUpdatePointRoleRequest
import com.xquare.v1servicepoint.point.api.dto.request.DomainSavePointRoleRequest
import com.xquare.v1servicepoint.point.api.dto.response.PointStatusResponse
import com.xquare.v1servicepoint.point.exception.PointNotFoundException
import com.xquare.v1servicepoint.point.exception.UserNotFoundException
import com.xquare.v1servicepoint.point.spi.PointSpi
import com.xquare.v1servicepoint.point.spi.PointStatusSpi
import com.xquare.v1servicepoint.point.Point
import com.xquare.v1servicepoint.point.api.dto.response.PointRuleListResponse
import com.xquare.v1servicepoint.point.spi.PointHistorySpi
import java.util.UUID

@UseCase
class PointApiImpl(
    private val pointStatusSpi: PointStatusSpi,
    private val pointSpi: PointSpi,
    private val pointHistorySpi: PointHistorySpi,
) : PointApi {

    override suspend fun queryPointStatus(userId: UUID): PointStatusResponse {
        val getUserPointStatus = pointStatusSpi.findByUserId(userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        return PointStatusResponse(
            goodPoint = getUserPointStatus.goodPoint,
            badPoint = getUserPointStatus.badPoint,
        )
    }

    override suspend fun updatePointRole(pointId: UUID, request: DomainUpdatePointRoleRequest) {
        val point = pointSpi.findByPointId(pointId)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)

        val updatedPoint = point.updatePointRole(
            reason = request.reason,
            type = request.type,
            point = request.point,
        )

        pointSpi.applyPointChanges(updatedPoint)
    }

    override suspend fun deletePointRole(pointId: UUID) {
        val point = pointSpi.findByPointId(pointId)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)
        val userPointHistory = pointHistorySpi.findAllByPointId(point.id)

        val pointStatus = pointStatusSpi.findByUserId(userPointHistory.first().userId)

        userPointHistory.forEach {
            pointHistorySpi.deleteByIdAndUserId(it)

            when (point.type) {
                true -> {
                    val minusGoodPoint = pointStatus?.minusGoodPoint(point.point)
                    pointStatusSpi.applyPointHistoryChanges(minusGoodPoint!!)
                }
                false -> {
                    val minusBadPoint = pointStatus?.minusBadPoint(point.point)
                    pointStatusSpi.applyPointHistoryChanges(minusBadPoint!!)
                }
            }
        }
        pointSpi.deletePointRole(point.id)
    }

    override suspend fun savePointRole(request: DomainSavePointRoleRequest) {
        val point = Point(
            id = UUID.randomUUID(),
            reason = request.reason,
            type = request.type,
            point = request.point,
        )
        pointSpi.savePointRole(point)
    }

    override suspend fun queryPointRoleList(type: Boolean): PointRuleListResponse {
        val pointList = pointSpi.findAllByType(type)
        return PointRuleListResponse(pointList)
    }
}
