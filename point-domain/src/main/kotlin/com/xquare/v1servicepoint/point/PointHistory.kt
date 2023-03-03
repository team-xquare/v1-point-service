package com.xquare.v1servicepoint.point

import com.xquare.v1servicepoint.annotation.Aggregate
import java.time.LocalDate
import java.util.UUID

@Aggregate
data class PointHistory(
    val id: UUID = UUID(0, 0),

    val date: LocalDate,

    val pointId: UUID,

    val userId: UUID,
)
