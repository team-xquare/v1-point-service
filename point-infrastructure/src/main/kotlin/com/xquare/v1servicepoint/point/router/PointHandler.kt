package com.xquare.v1servicepoint.point.router

import com.xquare.v1servicepoint.configuration.exception.UnAuthorizedException
import com.xquare.v1servicepoint.point.api.PointApi
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.util.*

@Component
class PointHandler(
    private val pointApi: PointApi,
) {
    suspend fun queryUserPointStatus(serverRequest: ServerRequest): ServerResponse {
        val userId = serverRequest.headers().firstHeader("Request-User-Id")
            ?: throw UnAuthorizedException("UnAuthorized")

        val pointStatus = pointApi.queryPointStatus(UUID.fromString(userId))
        return ServerResponse.ok().bodyValueAndAwait(pointStatus)
    }
}
