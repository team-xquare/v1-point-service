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
            GET("/student/{student-id}", pointHandler::queryUserPointStatus)
            POST("/student/{student-id}", pointHandler::saveUserPoint)
            DELETE("/student/{student-id}/history/{history-id}", pointHandler::deleteUserPoint)
            GET("/student/{student-id}/history", pointHandler::queryUserPointHistory)
            GET("/history", pointHandler::queryUserPointHistoryForStudent)
            PUT("/rule/{point-id}", pointHandler::updatePointRole)
            DELETE("/rule/{point-id}", pointHandler::deletePointRole)
            POST("/rule", pointHandler::savePointRole)
            GET("/rule", pointHandler::queryPointRuleList)
            POST("/{student-id}", pointHandler::savePointStatus)
//            GET("/excel", pointHandler::queryUserPointStatusExcel)
        }
    }
}
