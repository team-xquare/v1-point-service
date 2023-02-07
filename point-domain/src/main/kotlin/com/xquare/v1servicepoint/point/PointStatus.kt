package com.xquare.v1servicepoint.point

import com.xquare.v1servicepoint.annotation.Aggregate
import java.util.UUID

@Aggregate
data class PointStatus(
    val userId: UUID,

    val goodPoint: Int,

    val badPoint: Int,

    val penaltyLevel: Int,

    val isPenaltyRequired: Boolean
) {
    fun addGoodPoint(point: Int): PointStatus {
        return copy(goodPoint = goodPoint + point)
    }
    fun addBadPoint(point: Int): PointStatus {
        return copy(badPoint = badPoint + point)
    }
}
