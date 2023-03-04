package com.xquare.v1servicepoint.point.api.dto.response

import java.time.LocalDate

data class PointHistoryExcelElement(
    val date: LocalDate,
    val reason: String,
    val pointType: Boolean,
    val point: Int,
)
