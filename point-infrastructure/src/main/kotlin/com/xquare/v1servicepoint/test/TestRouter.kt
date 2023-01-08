package com.xquare.v1servicepoint.test

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class TestRouter(
    private val testHandler: TestHandler
) {

    @Bean
    fun pointBaseRouter(testHandler: TestHandler) = coRouter {
        "/points".nest {
            contentType(MediaType.APPLICATION_JSON)
            GET("/userId", testHandler::testfun)
        }

    }
}