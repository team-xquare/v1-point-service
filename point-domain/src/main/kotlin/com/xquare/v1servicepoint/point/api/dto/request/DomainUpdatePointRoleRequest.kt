package com.xquare.v1servicepoint.point.api.dto.request

data class DomainUpdatePointRoleRequest(
    val reason: String,
    val type: Boolean,
    val point: Int,
)
