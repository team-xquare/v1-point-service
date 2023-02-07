package com.xquare.v1servicepoint.point.mapper

import com.xquare.v1servicepoint.point.PointHistory
import com.xquare.v1servicepoint.point.entity.PointHistoryEntity
import com.xquare.v1servicepoint.point.exception.PointNotFoundException
import com.xquare.v1servicepoint.point.spi.PointSpi
import org.springframework.stereotype.Component

@Component
class PointHistoryMapperImpl(
    private val pointSpi: PointSpi,
    private val pointMapper: PointMapper,
) : PointHistoryMapper {
    override fun pointHistoryEntityToDomain(pointHistoryEntity: PointHistoryEntity): PointHistory {
        return PointHistory(
            id = pointHistoryEntity.id,
            date = pointHistoryEntity.date,
            pointId = pointHistoryEntity.point.id,
            userId = pointHistoryEntity.userId,
        )
    }

    override suspend fun pointHistoryDomainToEntity(pointHistory: PointHistory): PointHistoryEntity {
        val point = pointSpi.findByPointId(pointHistory.id)
            ?: throw PointNotFoundException(PointNotFoundException.POINT_NOT_FOUND)

        return PointHistoryEntity(
            id = pointHistory.id,
            date = pointHistory.date,
            point = pointMapper.pointDomainToEntity(point),
            userId = pointHistory.userId,
        )
    }
}
