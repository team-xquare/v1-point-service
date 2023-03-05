package com.xquare.v1servicepoint.point.entity.repository

import com.linecorp.kotlinjdsl.ReactiveQueryFactory
import com.linecorp.kotlinjdsl.deleteQuery
import com.linecorp.kotlinjdsl.listQuery
import com.linecorp.kotlinjdsl.query.HibernateMutinyReactiveQueryFactory
import com.linecorp.kotlinjdsl.querydsl.expression.col
import com.linecorp.kotlinjdsl.querydsl.from.join
import com.linecorp.kotlinjdsl.selectQuery
import com.linecorp.kotlinjdsl.singleQueryOrNull
import com.xquare.v1servicepoint.point.Point
import com.xquare.v1servicepoint.point.PointHistory
import com.xquare.v1servicepoint.point.api.dto.response.PointHistoryElement
import com.xquare.v1servicepoint.point.api.dto.response.PointHistoryExcelElement
import com.xquare.v1servicepoint.point.entity.PointEntity
import com.xquare.v1servicepoint.point.entity.PointHistoryEntity
import com.xquare.v1servicepoint.point.mapper.PointHistoryMapper
import com.xquare.v1servicepoint.point.mapper.PointMapper
import com.xquare.v1servicepoint.point.spi.PointHistorySpi
import com.xquare.v1servicepoint.point.spi.PointSpi
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.sql.Select
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID
import javax.persistence.criteria.JoinType

@Repository
class PointHistoryRepository(
    private val reactiveQueryFactory: HibernateMutinyReactiveQueryFactory,
    private val pointHistoryMapper: PointHistoryMapper,
    private val pointSpi: PointSpi,
    private val pointMapper: PointMapper,
) : PointHistorySpi {

    override suspend fun saveUserPoint(userId: UUID, pointId: UUID) {
        val pointDomainToSave = pointHistoryMapper.pointHistoryDomainToEntity(
            PointHistory(
                id = UUID.randomUUID(),
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
        val pointHistory = reactiveQueryFactory.transactionWithFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.findByIdAndStudentId(id, studentId)
        }

        return pointHistory?.let { pointHistoryMapper.pointHistoryEntityToDomain(it) }
    }

    private suspend fun ReactiveQueryFactory.findByIdAndStudentId(id: UUID, studentId: UUID): PointHistoryEntity? {
        return this.singleQueryOrNull<PointHistoryEntity> {
            select(entity(PointHistoryEntity::class))
            from(entity(PointHistoryEntity::class))
            where(
                col(PointHistoryEntity::id).equal(id)
                    .and(col(PointHistoryEntity::userId).equal(studentId)),
            )
        }
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

    override suspend fun findAllByUserIdAndType(userId: UUID, type: Boolean?): List<PointHistoryElement> {
        val pointHistory = reactiveQueryFactory.transactionWithFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.findAllByUserIdAndType(userId, type)
        }

        return pointHistory.map {
            PointHistoryElement(
                id = it.id,
                date = it.date,
                reason = it.reason,
                pointType = it.pointType,
                point = it.point,
            )
        }
    }

    private suspend fun ReactiveQueryFactory.findAllByUserIdAndType(
        userId: UUID,
        type: Boolean?,
    ): List<PointHistoryElement> {
        return this.listQuery {
            select(
                listOf(
                    col(PointHistoryEntity::id),
                    col(PointHistoryEntity::date),
                    col(PointEntity::reason),
                    col(PointEntity::type),
                    col(PointEntity::point),
                ),
            )
            from(entity(PointHistoryEntity::class))
            join(PointHistoryEntity::point, JoinType.LEFT)
            where(
                and(
                    col(PointHistoryEntity::userId).equal(userId),
                    type?.let { col(PointEntity::type).equal(type) },
                )
            )
        }
    }

    override suspend fun findAllByPointId(pointId: UUID): List<PointHistory> {
        return reactiveQueryFactory.transactionWithFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.findAllByPointIdIn(pointId)
        }.map { pointHistoryMapper.pointHistoryEntityToDomain(it) }
    }

    private suspend fun ReactiveQueryFactory.findAllByPointIdIn(pointId: UUID): List<PointHistoryEntity> {
        return this.selectQuery<PointHistoryEntity> {
            select(entity(PointHistoryEntity::class))
            from(entity(PointHistoryEntity::class))
            join(PointHistoryEntity::point, JoinType.LEFT)
            where(col(PointEntity::id).`in`(pointId))
        }.resultList()
    }

    override suspend fun findByUserId(userId: UUID): PointHistory? {
        return reactiveQueryFactory.transactionWithFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.findAllByUserId(userId)
        }?.let { pointHistoryMapper.pointHistoryEntityToDomain(it) }
    }

    private suspend fun ReactiveQueryFactory.findAllByUserId(userId: UUID): PointHistoryEntity? {
        return this.listQuery<PointHistoryEntity> {
            select(entity(PointHistoryEntity::class))
            from(entity(PointHistoryEntity::class))
            where(col(PointHistoryEntity::userId).equal(userId))
        }.firstOrNull()
    }

    override suspend fun findAllByIdAndType(idList: List<UUID>, type: Boolean?): List<PointHistoryExcelElement> {
        val pointHistory = reactiveQueryFactory.transactionWithFactory { _, reactiveQueryFactory ->
            reactiveQueryFactory.findAllByIdAndType(idList, type)
        }

        return pointHistory.map {
            PointHistoryExcelElement(
                date = it.date,
                reason = it.reason,
                pointType = it.pointType,
                point = it.point,
            )
        }
    }

    private suspend fun ReactiveQueryFactory.findAllByIdAndType(ids: List<UUID>, type: Boolean?): List<PointHistoryExcelElement> {
        return this.listQuery {
            select(
                listOf(
                    col(PointHistoryEntity::date),
                    col(PointEntity::reason),
                    col(PointEntity::type),
                    col(PointEntity::point),
                ),
            )
            from(entity(PointHistoryEntity::class))
            join(PointHistoryEntity::point, JoinType.LEFT)
            where(
                and(
                    col(PointHistoryEntity::userId).`in`(ids),
                    type?.let { col(PointEntity::type).equal(type) },
                ),
            )
        }
    }
}
