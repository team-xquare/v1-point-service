package com.xquare.v1servicepoint.point.entity.repository

import com.linecorp.kotlinjdsl.ReactiveQueryFactory
import com.linecorp.kotlinjdsl.query.HibernateMutinyReactiveQueryFactory
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.selectQuery
import com.xquare.v1servicepoint.point.PointStatus
import com.xquare.v1servicepoint.point.entity.PointStatusEntity
import com.xquare.v1servicepoint.point.mapper.PointStatusMapper
import com.xquare.v1servicepoint.point.spi.PointStatusSpi
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PointStatusRepository(
    private val reactiveQueryFactory: HibernateMutinyReactiveQueryFactory,
    private val pointStatusMapper: PointStatusMapper,
) : PointStatusSpi {
    override suspend fun findByUserId(userId: UUID): PointStatus? {
        val pointId = reactiveQueryFactory.withFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.findByUserIdIn(userId)
        }

        return pointId?.let { pointStatusMapper.pointStatusEntityToDomain(it) }
    }

    private suspend fun ReactiveQueryFactory.findByUserIdIn(id: UUID): PointStatusEntity? {
        return this.selectQuery<PointStatusEntity> {
            select(entity(PointStatusEntity::class))
            from(entity(PointStatusEntity::class))
            where(col(PointStatusEntity::userId).`in`(id))
        }.singleResult()
    }

    override suspend fun applyPointHistoryChanges(pointStatus: PointStatus): PointStatus {
        val pointStatusEntity = pointStatusMapper.pointStatusDomainToEntity(pointStatus)
        val updatePointStatusEntity = reactiveQueryFactory.transactionWithFactory { session, _ ->
            session.mergePointStatusEntity(pointStatusEntity)
        }

        return pointStatusMapper.pointStatusEntityToDomain(updatePointStatusEntity)
    }

    private suspend fun Mutiny.Session.mergePointStatusEntity(pointStatusEntity: PointStatusEntity) =
        this.merge(pointStatusEntity).awaitSuspending()
}
