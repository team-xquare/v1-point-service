package com.xquare.v1servicepoint.point

import com.xquare.v1servicepoint.annotation.Aggregate
import java.util.UUID

@Aggregate
class PointStatus(
    val userId: UUID,

    val goodPoint: Int,

    val badPoint: Int,

    val penaltyLevel: Int,

    val isPenaltyRequired: Boolean
)
