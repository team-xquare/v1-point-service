package com.xquare.v1servicepoint.point.spi.pointhistory

import com.xquare.v1servicepoint.annotation.Spi
import com.xquare.v1servicepoint.point.Point
import com.xquare.v1servicepoint.point.PointHistory
import java.util.UUID

@Spi
interface CommandPointHistorySpi {
    suspend fun saveUserPointHistory(userId: UUID, pointId: UUID)

    suspend fun saveUserListPointHistory(userId: UUID, pointIds: List<Point>)

    suspend fun deleteByIdAndUserId(pointHistory: PointHistory)
}
