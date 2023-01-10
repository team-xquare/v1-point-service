package com.xquare.v1servicepoint.test

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import java.util.*

@Component
class TestHandler {
    suspend fun testfun(serverRequest: ServerRequest): ServerResponse {
        val userId = UUID.fromString(serverRequest.pathVariable("userId"))
        val pointList = Point(
            badPoint = 12,
            goodPoint = 42
        )

        if (!userId.equals(null)) {
            return ServerResponse.ok().bodyValueAndAwait(pointList)
        }

        return ServerResponse.notFound().buildAndAwait()
    }
}