package com.xquare.v1servicepoint.point.api.dto.response

data class UserResponse(
    val userInfoList: List<UserInfoListElement>,
) {
    data class UserInfoListElement(
        val name: String,
        val grade: Int,
        val classNum: Int,
        val num: Int,
    )
}
