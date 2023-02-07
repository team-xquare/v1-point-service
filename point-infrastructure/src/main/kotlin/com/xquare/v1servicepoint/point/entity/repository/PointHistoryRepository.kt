package com.xquare.v1servicepoint.point.entity.repository

import com.github.f4b6a3.uuid.UuidCreator
import com.linecorp.kotlinjdsl.ReactiveQueryFactory
import com.linecorp.kotlinjdsl.query.HibernateMutinyReactiveQueryFactory
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.selectQuery
import com.xquare.v1servicepoint.point.Point
import com.xquare.v1servicepoint.point.PointHistory
import com.xquare.v1servicepoint.point.entity.PointHistoryEntity
import com.xquare.v1servicepoint.point.mapper.PointHistoryMapper
import com.xquare.v1servicepoint.point.spi.PointHistorySpi
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
class PointHistoryRepository(
    private val reactiveQueryFactory: HibernateMutinyReactiveQueryFactory,
    private val pointHistoryMapper: PointHistoryMapper,
) : PointHistorySpi {

    override suspend fun saveUserPoint(userId: UUID, pointId: UUID) {
        val pointDomainToSave = pointHistoryMapper.pointHistoryDomainToEntity(
            PointHistory(
                id = UuidCreator.getTimeOrderedEpoch(),
                date = LocalDate.now(),
                userId = userId,
                pointId = findByPointId(pointId).id,
            ),
        )

        reactiveQueryFactory.transactionWithFactory { session, _ ->
            session.persistPointHistoryEntityConcurrently(pointDomainToSave)
        }
    }

    private suspend fun Mutiny.Session.persistPointHistoryEntityConcurrently(pointHistoryEntity: PointHistoryEntity) =
        this@persistPointHistoryEntityConcurrently.persist(pointHistoryEntity).awaitSuspending()

    override suspend fun findByPointId(pointId: UUID): Point {
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
