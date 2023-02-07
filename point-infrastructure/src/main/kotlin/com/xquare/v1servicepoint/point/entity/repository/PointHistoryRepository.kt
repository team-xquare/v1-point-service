package com.xquare.v1servicepoint.point.entity.repository

import com.github.f4b6a3.uuid.UuidCreator
import com.linecorp.kotlinjdsl.query.HibernateMutinyReactiveQueryFactory
import com.xquare.v1servicepoint.point.PointHistory
import com.xquare.v1servicepoint.point.entity.PointHistoryEntity
import com.xquare.v1servicepoint.point.mapper.PointHistoryMapper
import com.xquare.v1servicepoint.point.spi.PointHistorySpi
import io.smallrye.mutiny.coroutines.awaitSuspending
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
                pointId = pointId,
            ),
        )

        reactiveQueryFactory.transactionWithFactory { session, _ ->
            session.persistPointHistoryEntityConcurrently(pointDomainToSave)
        }
    }

    private suspend fun Mutiny.Session.persistPointHistoryEntityConcurrently(pointHistoryEntity: PointHistoryEntity) =
        this@persistPointHistoryEntityConcurrently.persist(pointHistoryEntity).awaitSuspending()
}
