package com.xquare.v1servicepoint.point.entity.repository

import com.linecorp.kotlinjdsl.ReactiveQueryFactory
import com.linecorp.kotlinjdsl.query.HibernateMutinyReactiveQueryFactory
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.selectQuery
import com.xquare.v1servicepoint.point.entity.PointEntity
import com.xquare.v1servicepoint.point.spi.PointHistoryRepositorySpi
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PointHistoryRepository(
    private val reactiveQueryFactory: HibernateMutinyReactiveQueryFactory,
) : PointHistoryRepositorySpi {
    suspend fun findByPointId(pointId: UUID): PointEntity {
        return reactiveQueryFactory.withFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.findByIdIn(pointId)
        }
    }

    private suspend fun ReactiveQueryFactory.findByIdIn(id: UUID): PointEntity { // 코루틴 동작 안하도록 변경해야 mapper도 변경 할 수 있음
        return this.selectQuery<PointEntity> {
            select(entity(PointEntity::class))
            from(entity(PointEntity::class))
            where(col(PointEntity::id).`in`(id))
        }.singleResult()
    }
}
