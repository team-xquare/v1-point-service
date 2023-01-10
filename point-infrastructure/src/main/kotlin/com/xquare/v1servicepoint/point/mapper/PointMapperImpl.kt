package com.xquare.v1servicepoint.point.mapper

import com.xquare.v1servicepoint.point.Point
import com.xquare.v1servicepoint.point.entity.PointEntity

class PointMapperImpl : PointMapper {

    override fun pointEntityToDomain(pointEntity: PointEntity): Point {
        return Point(
            id = pointEntity.id,
            reason = pointEntity.reason,
            point = pointEntity.point,
            type = pointEntity.type
        )
    }

    override fun pointDomainToEntity(point: Point): PointEntity {
        return PointEntity(
            id = point.id,
            reason = point.reason,
            point = point.point,
            type = point.type
        )
    }
}
