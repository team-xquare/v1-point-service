package com.xquare.v1servicepoint.point.spi

import com.xquare.v1servicepoint.annotation.Spi
import com.xquare.v1servicepoint.point.api.dto.response.UserResponse.UserInfoListElement
import java.util.UUID

@Spi
interface UserSpi {
    suspend fun getUserInfo(userId: List<UUID>): List<UserInfoListElement>
    suspend fun getAllStudent(): List<UserInfoListElement>
}
