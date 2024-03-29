package com.xquare.v1servicepoint.point.spi.pointstatus

import com.xquare.v1servicepoint.annotation.Spi
import com.xquare.v1servicepoint.point.PointStatus
import java.util.UUID

@Spi
interface QueryPointStatusSpi {
    suspend fun findByUserId(userId: UUID): PointStatus?
    suspend fun findAll(): List<PointStatus>
    suspend fun findAllByPenaltyLevel(penaltyLevel: Int?): List<PointStatus>
}
