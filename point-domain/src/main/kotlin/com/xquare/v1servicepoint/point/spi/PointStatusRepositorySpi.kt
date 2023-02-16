package com.xquare.v1servicepoint.point.spi

import com.xquare.v1servicepoint.annotation.Spi
import com.xquare.v1servicepoint.point.PointStatus
import java.util.UUID

@Spi
interface PointStatusRepositorySpi {

    suspend fun findByUserId(userId: UUID): PointStatus?
    suspend fun applyPointHistoryChanges(pointStatus: PointStatus): PointStatus
}
