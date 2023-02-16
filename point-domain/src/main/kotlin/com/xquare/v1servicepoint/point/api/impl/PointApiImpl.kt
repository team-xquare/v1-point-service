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
import java.util.UUID

@UseCase
class PointApiImpl(
    private val pointStatusSpi: PointStatusSpi,
    private val pointSpi: PointSpi,
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
            ?: throw PointNotFoundException((PointNotFoundException.POINT_NOT_FOUND))

        val updatePoint = point.updatePointRole(
            reason = request.reason,
            type = request.type,
            point = request.point,
        )

        pointSpi.applyPointChanges(updatePoint)
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
}
