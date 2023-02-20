package com.xquare.v1servicepoint.point.api.dto.response

import com.xquare.v1servicepoint.point.Point

data class PointRuleListResponse(
    val rules: List<Point>,
)
