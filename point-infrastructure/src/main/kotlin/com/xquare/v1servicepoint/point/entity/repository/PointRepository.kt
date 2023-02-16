package com.xquare.v1servicepoint.point.entity.repository

import com.linecorp.kotlinjdsl.ReactiveQueryFactory
import com.linecorp.kotlinjdsl.deleteQuery
import com.linecorp.kotlinjdsl.query.HibernateMutinyReactiveQueryFactory
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.singleQueryOrNull
import com.xquare.v1servicepoint.point.Point
import com.xquare.v1servicepoint.point.entity.PointEntity
import com.xquare.v1servicepoint.point.mapper.PointMapper
import com.xquare.v1servicepoint.point.spi.PointSpi
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PointRepository(
    private val reactiveQueryFactory: HibernateMutinyReactiveQueryFactory,
    private val pointMapper: PointMapper,
) : PointSpi {

    override suspend fun findByPointId(pointId: UUID): Point? {
        val pointEntity = reactiveQueryFactory.withFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.findByIdIn(pointId)
        }

        return pointEntity?.let { pointMapper.pointEntityToDomain(it) }
    }

    private suspend fun ReactiveQueryFactory.findByIdIn(id: UUID): PointEntity? { // 코루틴 동작 안하도록 변경해야 mapper도 변경 할 수 있음
        return this.singleQueryOrNull<PointEntity> {
            select(entity(PointEntity::class))
            from(entity(PointEntity::class))
            where(col(PointEntity::id).`in`(id))
        }
    }

    override suspend fun deletePointRole(pointId: UUID) {
        reactiveQueryFactory.withFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.deletePointRoleIn(pointId)
        }
    }

    private suspend fun ReactiveQueryFactory.deletePointRoleIn(pointId: UUID) {
        this.deleteQuery<PointEntity> {
            where(col(PointEntity::id).`in`(pointId))
        }
    }

    override suspend fun savePointRole(point: Point) {
        val pointEntity = pointMapper.pointDomainToEntity(point)
        reactiveQueryFactory.transactionWithFactory { session, _ ->
            session.persistPointEntityConcurrently(pointEntity)
        }
    }

    private suspend fun Mutiny.Session.persistPointEntityConcurrently(point: PointEntity) =
        this@persistPointEntityConcurrently.persist(point).awaitSuspending()
}
