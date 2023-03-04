package com.xquare.v1servicepoint.point.api.dto.response

import java.time.LocalDate
import java.util.*

data class UserResponse(
    val users: List<UserInfoListElement>,
) {
    data class UserInfoListElement(
        val id: UUID,
        val accountId: String,
        val password: String,
        val name: String,
        val grade: Int,
        val classNum: Int,
        val num: Int,
        val birthDay: LocalDate,
        val profileFileName: String?,
    )
}
