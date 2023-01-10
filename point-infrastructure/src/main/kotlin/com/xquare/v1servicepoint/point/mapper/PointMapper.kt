package com.xquare.v1servicepoint.point.mapper

import com.xquare.v1servicepoint.point.Point
import com.xquare.v1servicepoint.point.entity.PointEntity

interface PointMapper {
    fun pointEntityToDomain(pointEntity: PointEntity): Point
    fun pointDomainToEntity(point: Point): PointEntity
}