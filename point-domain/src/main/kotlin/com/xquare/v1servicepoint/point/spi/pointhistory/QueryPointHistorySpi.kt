package com.xquare.v1servicepoint.point.spi.pointhistory

import com.xquare.v1servicepoint.annotation.Spi
import com.xquare.v1servicepoint.point.PointHistory
import com.xquare.v1servicepoint.point.api.dto.response.PointHistoryElement
import com.xquare.v1servicepoint.point.api.dto.response.PointHistoryExcelElement
import java.util.UUID

@Spi
interface QueryPointHistorySpi {
    suspend fun findByIdAndStudentId(id: UUID, studentId: UUID): PointHistory?

    suspend fun deleteByIdAndUserId(pointHistory: PointHistory)

    suspend fun findAllByUserIdAndType(userId: UUID, type: Boolean?): List<PointHistoryElement>

    suspend fun findAllByPointId(pointId: UUID): List<PointHistory>

    suspend fun findByUserId(userId: UUID): PointHistory?

    suspend fun findAllByType(type: Boolean?): List<PointHistoryExcelElement>
}
