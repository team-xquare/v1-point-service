package com.xquare.v1servicepoint.point.mapper

import com.xquare.v1servicepoint.point.PointStatus
import com.xquare.v1servicepoint.point.entity.PointStatusEntity

interface PointStatusMapper {
    fun pointStatusEntityToDomain(pointStatusEntity: PointStatusEntity): PointStatus
    fun pointStatusDomainToEntity(pointStatus: PointStatus): PointStatusEntity
}
