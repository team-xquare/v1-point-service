package com.xquare.v1servicepoint.point.mapper

import com.xquare.v1servicepoint.point.PointHistory
import com.xquare.v1servicepoint.point.entity.PointHistoryEntity

interface PointHistoryMapper {
    fun pointHistoryEntityToDomain(pointHistoryEntity: PointHistoryEntity): PointHistory
    fun pointHistoryDomainToEntity(pointHistory: PointHistory): PointHistoryEntity
}