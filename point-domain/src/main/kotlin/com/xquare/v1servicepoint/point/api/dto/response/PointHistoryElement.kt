package com.xquare.v1servicepoint.point.api.dto.response

import java.time.LocalDate
import java.util.UUID

data class PointHistoryElement(
    val id: UUID,
    val date: LocalDate,
    val reason: String,
    val pointType: Boolean,
    val point: Int,
)
