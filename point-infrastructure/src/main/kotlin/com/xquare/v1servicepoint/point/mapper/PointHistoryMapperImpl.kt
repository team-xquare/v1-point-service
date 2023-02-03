package com.xquare.v1servicepoint.point.mapper

import com.xquare.v1servicepoint.point.PointHistory
import com.xquare.v1servicepoint.point.entity.PointHistoryEntity
import com.xquare.v1servicepoint.point.entity.repository.PointHistoryRepository

class PointHistoryMapperImpl(
    private val pointHistoryRepository: PointHistoryRepository,
) : PointHistoryMapper {
    override fun pointHistoryEntityToDomain(pointHistoryEntity: PointHistoryEntity): PointHistory {
        return PointHistory(
            id = pointHistoryEntity.id,
            pointId = pointHistoryEntity.point.id,
            userId = pointHistoryEntity.userId,
        )
    }

    override suspend fun pointHistoryDomainToEntity(pointHistory: PointHistory): PointHistoryEntity {
        val point = pointHistoryRepository.findByPointId(pointHistory.id)

        return PointHistoryEntity(
            id = pointHistory.id,
            point = point,
            userId = pointHistory.userId,
        )
    }
}
