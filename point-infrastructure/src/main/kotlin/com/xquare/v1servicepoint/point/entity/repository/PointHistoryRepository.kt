package com.xquare.v1servicepoint.point.entity.repository

import com.github.f4b6a3.uuid.UuidCreator
import com.linecorp.kotlinjdsl.ReactiveQueryFactory
import com.linecorp.kotlinjdsl.deleteQuery
import com.linecorp.kotlinjdsl.query.HibernateMutinyReactiveQueryFactory
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.selectQuery
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

    override suspend fun findByIdAndStudentId(id: UUID, studentId: UUID): PointHistory? {
        return reactiveQueryFactory.transactionWithFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.findByIdAndStudentId(id, studentId)
        }
    }

    private suspend fun ReactiveQueryFactory.findByIdAndStudentId(id: UUID, studentId: UUID): PointHistory {
        return this.selectQuery<PointHistory> {
            where(
                col(PointHistory::id).equal(id)
                    .and(col(PointHistory::userId).equal(studentId)),
            )
        }.singleResult()
    }

    override suspend fun deleteByIdAndUserId(pointHistory: PointHistory) {
        reactiveQueryFactory.transactionWithFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.deleteWithIdAndUserId(pointHistory.id, pointHistory.userId)
        }
    }

    private suspend fun ReactiveQueryFactory.deleteWithIdAndUserId(id: UUID, studentId: UUID) {
        this.deleteQuery<PointHistoryEntity> {
            where(
                col(PointHistoryEntity::id).equal(id)
                    .and(col(PointHistoryEntity::userId).equal(studentId)),
            )
        }.executeUpdate()
    }
}
