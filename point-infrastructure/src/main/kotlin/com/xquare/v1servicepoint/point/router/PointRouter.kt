package com.xquare.v1servicepoint.point.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class PointRouter {

    @Bean
    fun userBaseRouter(pointHandler: PointHandler) = coRouter {
        "/points".nest {
            contentType(MediaType.APPLICATION_JSON)
            GET("", pointHandler::queryUserPointStatus)
        }
    }
}