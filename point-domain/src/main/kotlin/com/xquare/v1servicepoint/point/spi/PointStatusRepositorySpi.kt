package com.xquare.v1servicepoint.point.spi

import com.xquare.v1servicepoint.annotation.Spi
import com.xquare.v1servicepoint.point.PointStatus
import java.util.UUID

@Spi
interface PointStatusRepositorySpi {

    suspend fun findByUserId(userId: UUID): PointStatus?
    suspend fun applyPointStatusChanges(pointStatus: PointStatus): PointStatus
    suspend fun savePointStatus(pointStatus: PointStatus)
    suspend fun findAll(): List<PointStatus>
    suspend fun findAllByPenaltyLevel(penaltyLevel: Int?): List<PointStatus>
}
