package com.xquare.v1servicepoint.point.entity.repository

import com.linecorp.kotlinjdsl.ReactiveQueryFactory
import com.linecorp.kotlinjdsl.query.HibernateMutinyReactiveQueryFactory
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.selectQuery
import com.xquare.v1servicepoint.point.Point
import com.xquare.v1servicepoint.point.spi.PointSpi
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PointRepository(
    private val reactiveQueryFactory: HibernateMutinyReactiveQueryFactory
) : PointSpi {

    override suspend fun findByPointId(pointId: UUID): Point? {
        return reactiveQueryFactory.withFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.findByIdIn(pointId)
        }
    }

    private suspend fun ReactiveQueryFactory.findByIdIn(id: UUID): Point { // 코루틴 동작 안하도록 변경해야 mapper도 변경 할 수 있음
        return this.selectQuery<Point> {
            select(entity(Point::class))
            from(entity(Point::class))
            where(col(Point::id).`in`(id))
        }.singleResult()
    }
}
