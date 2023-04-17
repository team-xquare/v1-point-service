package com.xquare.v1servicepoint.point.api.impl

import com.xquare.v1servicepoint.annotation.UseCase
import com.xquare.v1servicepoint.point.Point
import com.xquare.v1servicepoint.point.api.PointApi
import com.xquare.v1servicepoint.point.api.dto.request.DomainSavePointRoleRequest
import com.xquare.v1servicepoint.point.api.dto.request.DomainUpdatePointRoleRequest
import com.xquare.v1servicepoint.point.api.dto.response.PointRuleListResponse
import com.xquare.v1servicepoint.point.exception.PointNotFoundException
import com.xquare.v1servicepoint.point.spi.point.CommandPointSpi
import com.xquare.v1servicepoint.point.spi.point.QueryPointSpi
import com.xquare.v1servicepoint.point.spi.pointhistory.QueryPointHistorySpi
import com.xquare.v1servicepoint.point.spi.pointstatus.CommandPointStatusSpi
import com.xquare.v1servicepoint.point.spi.pointstatus.QueryPointStatusSpi
import java.util.UUID

@UseCase
class PointApiImpl(
    private val queryPointSpi: QueryPointSpi,
    private val commandPointSpi: CommandPointSpi,
    private val queryPointHistorySpi: QueryPointHistorySpi,
    private val queryPointStatusSpi: QueryPointStatusSpi,
    private val commandPointStatusSpi: CommandPointStatusSpi,
) : PointApi {

    override suspend fun updatePointRole(pointId: UUID, request: DomainUpdatePointRoleRequest) {
        val point = queryPointSpi.findByPointId(pointId)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)

        val updatedPoint = point.updatePointRole(
            reason = request.reason,
            type = request.type,
            point = request.point,
        )

        commandPointSpi.applyPointChanges(updatedPoint)
    }

    override suspend fun deletePointRole(pointId: UUID) {
        val point = queryPointSpi.findByPointId(pointId)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)
        val userPointHistory = queryPointHistorySpi.findAllByPointId(point.id)

        userPointHistory.forEach {
            val pointStatus = queryPointStatusSpi.findByUserId(it.userId)
            queryPointHistorySpi.deleteByIdAndUserId(it)

            when (point.type) {
                true -> {
                    val minusGoodPoint = pointStatus?.minusGoodPoint(point.point)
                    commandPointStatusSpi.applyPointStatusChanges(minusGoodPoint!!)
                }

                false -> {
                    val minusBadPoint = pointStatus?.minusBadPoint(point.point)
                    commandPointStatusSpi.applyPointStatusChanges(minusBadPoint!!)
                }
            }
        }
        commandPointSpi.deletePointRole(point.id)
    }

    override suspend fun savePointRole(request: DomainSavePointRoleRequest) {
        val point = Point(
            id = UUID.randomUUID(),
            reason = request.reason,
            type = request.type,
            point = request.point,
        )
        commandPointSpi.savePointRole(point)
    }

    override suspend fun queryPointRoleList(type: Boolean): PointRuleListResponse {
        val pointList = queryPointSpi.findAllByType(type)
        return PointRuleListResponse(pointList)
    }
}
