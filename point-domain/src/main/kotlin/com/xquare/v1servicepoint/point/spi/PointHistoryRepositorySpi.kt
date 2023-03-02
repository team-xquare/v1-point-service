package com.xquare.v1servicepoint.point.spi

import com.xquare.v1servicepoint.annotation.Spi
import com.xquare.v1servicepoint.point.PointHistory
import com.xquare.v1servicepoint.point.api.dto.response.PointHistoryElement
import java.util.UUID

@Spi
interface PointHistoryRepositorySpi {
    suspend fun saveUserPoint(userId: UUID, pointId: UUID)

    suspend fun findByIdAndStudentId(id: UUID, studentId: UUID): PointHistory?

    suspend fun deleteByIdAndUserId(pointHistory: PointHistory)

    suspend fun findAllByUserIdAndType(userId: UUID, type: Boolean?): List<PointHistoryElement>

    suspend fun findAllByPointId(pointId: UUID): List<PointHistory>
}
