package com.xquare.v1servicepoint.point.api.dto.response

import java.util.UUID

data class PointStudentStatusElement(
    val id: UUID,
    val name: String,
    val num: String,
    val goodPoint: Int,
    val badPoint: Int,
    val penaltyLevel: Int,
    val isPenaltyRequired: Boolean,
)
