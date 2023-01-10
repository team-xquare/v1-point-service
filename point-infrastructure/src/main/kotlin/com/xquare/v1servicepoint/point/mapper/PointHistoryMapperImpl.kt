package com.xquare.v1servicepoint.point.mapper

import com.xquare.v1servicepoint.point.PointHistory
import com.xquare.v1servicepoint.point.entity.PointHistoryEntity
import com.xquare.v1servicepoint.point.entity.repository.PointRepository

class PointHistoryMapperImpl(
    private val pointRepository: PointRepository
) : PointHistoryMapper {
    override fun pointHistoryEntityToDomain(pointHistoryEntity: PointHistoryEntity): PointHistory {
        TODO("Not yet implemented")
    }

    override fun pointHistoryDomainToEntity(pointHistory: PointHistory): PointHistoryEntity {
        TODO("Not yet implemented")
    }
}
