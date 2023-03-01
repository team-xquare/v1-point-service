package com.xquare.v1servicepoint.point.api.dto.response

data class PointHistoryListStudentResponse(
    val goodPoint: Int,
    val badPoint: Int,
    val pointHistories: List<PointHistoryElement>,
)
