package com.xquare.v1servicepoint.point.router.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import java.time.LocalDate
import java.util.UUID

data class DomainUserResponse(
    val users: List<UserInfoListElement>,
) {
    data class UserInfoListElement(
        val id: UUID,
        @JsonProperty("account_id")
        val accountId: String,
        val password: String,
        val name: String,
        val grade: Int,
        @JsonProperty("class_num")
        val classNum: Int,
        val num: Int,
        @JsonProperty("birth_day")
        @JsonDeserialize(using = LocalDateDeserializer::class)
        val birthDay: LocalDate,
        @JsonProperty("profile_file_name")
        val profileFileName: String?,
    )
}
