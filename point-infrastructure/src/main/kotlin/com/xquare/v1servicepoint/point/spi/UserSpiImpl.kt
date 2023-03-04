package com.xquare.v1servicepoint.point.spi

import com.xquare.v1servicepoint.point.api.dto.response.UserResponse
import com.xquare.v1servicepoint.point.exception.UserRequsetFailedException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.util.UUID



@Component
class UserSpiImpl(
    private val webClient: WebClient,
    @Value("\${service.user.host}")
    private val userHost: String,
    @Value("\${service.scheme}")
    private val scheme: String,
): UserSpi {
    override suspend fun getUserInfo(userId: List<UUID>): List<UserResponse.UserInfoListElement> {
        println(scheme)
        println(userHost)

        val multiValueMap = createMultiValueMap(userId.map { it.toString() })

        println(multiValueMap)
        return webClient.get().uri {
            it.scheme(scheme)
                .host(userHost)
                .path("/id")
                .queryParams(multiValueMap)
                .build()
        }.retrieve()
            .onStatus(HttpStatus::isError) {
                throw UserRequsetFailedException("Failed request to get user point", it.rawStatusCode())
            }
            .awaitBody<UserResponse>().let { it ->
                it.userInfoList.map {
                    UserResponse.UserInfoListElement(
                        name = it.name,
                        grade = it.grade,
                        classNum = it.classNum,
                        num = it.num
                    )
                }
            }
    }
}

fun createMultiValueMap(userIds: List<String>): LinkedMultiValueMap<String, String> {
    val multiValueMap = LinkedMultiValueMap<String, String>()
    for (value in userIds) {
        multiValueMap.add("userId", value)
    }
    return multiValueMap
}
