package com.xquare.v1servicepoint.point.entity.repository

import com.linecorp.kotlinjdsl.ReactiveQueryFactory
import com.linecorp.kotlinjdsl.query.HibernateMutinyReactiveQueryFactory
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.selectQuery
import com.xquare.v1servicepoint.point.PointStatus
import com.xquare.v1servicepoint.point.spi.PointStatusRepositorySpi
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PointStatusRepository(
    private val reactiveQueryFactory: HibernateMutinyReactiveQueryFactory

) : PointStatusRepositorySpi {
    override suspend fun findByUserId(userId: UUID): PointStatus? {
        return reactiveQueryFactory.withFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.findByUserIdIn(userId)
        }
    }

    private suspend fun ReactiveQueryFactory.findByUserIdIn(id: UUID): PointStatus {
        return this.selectQuery<PointStatus> {
            select(entity(PointStatus::class))
            from(entity(PointStatus::class))
            where(col(PointStatus::userId).`in`(id))
        }.singleResult()
    }
}

