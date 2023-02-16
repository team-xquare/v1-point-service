package com.xquare.v1servicepoint.point.router.dto

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class SavePointRoleRequest(
    @field:NotNull
    @field:Size(max = 20)
    val reason: String,

    @field:NotNull
    val type: Boolean,

    @field:NotNull
    val point: Int,
)
