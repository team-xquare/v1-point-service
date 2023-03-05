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

    fun minusGoodPoint(point: Int): PointStatus {
        return copy(goodPoint = goodPoint - point)
    }

    fun minusBadPoint(point: Int): PointStatus {
        return copy(badPoint = badPoint - point)
    }

    fun penaltyEducationComplete(): PointStatus {
        return copy(isPenaltyRequired = false)
    }

    fun penaltyEducationStart(): PointStatus {
        return copy(isPenaltyRequired = true)
    }

    fun penaltyLevelOne(): PointStatus {
        return copy(penaltyLevel = 1)
    }

    fun penaltyLevelTwo(): PointStatus {
        return copy(penaltyLevel = 2)
    }

    fun penaltyLevelThree(): PointStatus {
        return copy(penaltyLevel = 3)
    }

    fun penaltyLevelFour(): PointStatus {
        return copy(penaltyLevel = 4)
    }

    fun penaltyLeveFive(): PointStatus {
        return copy(penaltyLevel = 5)
    }
}
