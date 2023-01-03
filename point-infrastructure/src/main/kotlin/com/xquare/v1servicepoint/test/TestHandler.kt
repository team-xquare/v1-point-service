package com.xquare.v1servicepoint.test

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class TestHandler {
    suspend fun testfun(serverRequest: ServerRequest): ServerResponse {

        val pointList = Point(
            badPoint = 12,
            goodPoint = 42
        )

        return ServerResponse.ok().bodyValueAndAwait(pointList)
    }
}