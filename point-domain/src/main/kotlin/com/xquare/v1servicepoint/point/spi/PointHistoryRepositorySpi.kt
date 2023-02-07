package com.xquare.v1servicepoint.point.spi

import com.xquare.v1servicepoint.annotation.Spi
import java.util.UUID

@Spi
interface PointHistoryRepositorySpi {
    suspend fun saveUserPoint(userId: UUID, pointId: UUID)
}
