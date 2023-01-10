package com.xquare.v1servicepoint.point

import com.xquare.v1servicepoint.annotation.Aggregate
import java.util.UUID

@Aggregate
class Point(
    val id: UUID,

    val reason: String,

    val point: Int,

    val type: Int
)
