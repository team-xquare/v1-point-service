package com.xquare.v1servicepoint.point.entity.repository

import com.linecorp.kotlinjdsl.ReactiveQueryFactory
import com.linecorp.kotlinjdsl.listQuery
import com.linecorp.kotlinjdsl.query.HibernateMutinyReactiveQueryFactory
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.singleQueryOrNull
import com.xquare.v1servicepoint.point.PointStatus
import com.xquare.v1servicepoint.point.entity.PointStatusEntity
import com.xquare.v1servicepoint.point.mapper.PointStatusMapper
import com.xquare.v1servicepoint.point.spi.pointstatus.PointStatusSpi
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.stereotype.Repository
import java.util.UUID

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
        return this.singleQueryOrNull<PointStatusEntity> {
            select(entity(PointStatusEntity::class))
            from(entity(PointStatusEntity::class))
            where(col(PointStatusEntity::userId).`in`(id))
        }
    }

    override suspend fun applyPointStatusChanges(pointStatus: PointStatus): PointStatus {
        val pointStatusEntity = pointStatusMapper.pointStatusDomainToEntity(pointStatus)
        val updatePointStatusEntity = reactiveQueryFactory.transactionWithFactory { session, _ ->
            session.mergePointStatusEntity(pointStatusEntity)
        }

        return pointStatusMapper.pointStatusEntityToDomain(updatePointStatusEntity)
    }

    private suspend fun Mutiny.Session.mergePointStatusEntity(pointStatusEntity: PointStatusEntity) =
        this.merge(pointStatusEntity).awaitSuspending()

    override suspend fun savePointStatus(pointStatus: PointStatus) {
        val pointStatusEntity = pointStatusMapper.pointStatusDomainToEntity(pointStatus)
        reactiveQueryFactory.transactionWithFactory { session, _ ->
            session.persistPointStatusEntityConcurrently(pointStatusEntity)
        }
    }

    private suspend fun Mutiny.Session.persistPointStatusEntityConcurrently(pointStatusEntity: PointStatusEntity) =
        this@persistPointStatusEntityConcurrently.persist(pointStatusEntity).awaitSuspending()

    override suspend fun findAll(): List<PointStatus> {
        val pointStatusEntities = reactiveQueryFactory.withFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.findAllPointStatus()
        }
        return pointStatusEntities.map { pointStatusMapper.pointStatusEntityToDomain(it) }
    }

    private suspend fun ReactiveQueryFactory.findAllPointStatus(): List<PointStatusEntity> {
        return this.listQuery<PointStatusEntity> {
            select(entity(PointStatusEntity::class))
            from(entity(PointStatusEntity::class))
        }
    }

    override suspend fun findAllByPenaltyLevel(penaltyLevel: Int?): List<PointStatus> {
        val pointStatusEntities = reactiveQueryFactory.withFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.findAllByPenaltyLevel(penaltyLevel)
        }

        return pointStatusEntities.map { pointStatusMapper.pointStatusEntityToDomain(it) }
    }

    private suspend fun ReactiveQueryFactory.findAllByPenaltyLevel(penaltyLevel: Int?): List<PointStatusEntity> {
        return this.listQuery {
            select(entity(PointStatusEntity::class))
            from(entity(PointStatusEntity::class))
            where(
                and(
                    penaltyLevel?.let { col(PointStatusEntity::penaltyLevel).equal(it) },
                ),
            )
        }
    }
}
