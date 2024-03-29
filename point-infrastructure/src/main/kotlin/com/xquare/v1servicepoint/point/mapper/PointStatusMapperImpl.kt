package com.xquare.v1servicepoint.point.mapper

import com.xquare.v1servicepoint.point.PointStatus
import com.xquare.v1servicepoint.point.entity.PointStatusEntity
import org.springframework.stereotype.Component

@Component
class PointStatusMapperImpl : PointStatusMapper {

    override fun pointStatusEntityToDomain(pointStatusEntity: PointStatusEntity): PointStatus {
        return PointStatus(
            userId = pointStatusEntity.userId,
            goodPoint = pointStatusEntity.goodPoint,
            badPoint = pointStatusEntity.badPoint,
            penaltyLevel = pointStatusEntity.penaltyLevel,
            isPenaltyRequired = pointStatusEntity.isPenaltyRequired,
        )
    }

    override fun pointStatusDomainToEntity(pointStatus: PointStatus): PointStatusEntity {
        return PointStatusEntity(
            userId = pointStatus.userId,
            goodPoint = pointStatus.goodPoint,
            badPoint = pointStatus.badPoint,
            penaltyLevel = pointStatus.penaltyLevel,
            isPenaltyRequired = pointStatus.isPenaltyRequired,
        )
    }
}
