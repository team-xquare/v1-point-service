package com.xquare.v1servicepoint.point.router

import com.xquare.v1servicepoint.configuration.exception.UnAuthorizedException
import com.xquare.v1servicepoint.point.api.PointApi
import com.xquare.v1servicepoint.point.api.PointHistoryApi
import com.xquare.v1servicepoint.point.api.dto.request.DomainGivePointUserRequest
import com.xquare.v1servicepoint.point.router.dto.SaveUserPointRequest
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import java.net.URI
import java.util.UUID

@Component
class PointHandler(
    private val pointApi: PointApi,
    private val pointHistoryApi: PointHistoryApi,
) {
    suspend fun queryUserPointStatus(serverRequest: ServerRequest): ServerResponse {
        val userId = serverRequest.headers().firstHeader("Request-User-Id")
            ?: throw UnAuthorizedException("UnAuthorized")

        val pointStatus = pointApi.queryPointStatus(UUID.fromString(userId))
        return ServerResponse.ok().bodyValueAndAwait(pointStatus)
    }

    suspend fun saveUserPoint(serverRequest: ServerRequest): ServerResponse {
        val userId = serverRequest.pathVariable("student-id")

        val givePointUserRequest = serverRequest.getSavePointRequestBody()
        val domainRequest = givePointUserRequest.toDomainRequest()

        pointHistoryApi.saveUserPoint(UUID.fromString(userId), domainRequest)
        return ServerResponse.created(URI("/points/student/{student-id}")).buildAndAwait()
    }

    private suspend fun ServerRequest.getSavePointRequestBody() =
        this.bodyToMono<SaveUserPointRequest>().awaitSingle()

    private fun SaveUserPointRequest.toDomainRequest() = DomainGivePointUserRequest(
        pointId = this.pointId,
    )

    suspend fun deleteUserPoint(serverRequest: ServerRequest): ServerResponse {
        val studentId = serverRequest.pathVariable("student-id")
        val historyId = serverRequest.pathVariable("history-id")

        pointHistoryApi.deleteUserPoint(UUID.fromString(studentId), UUID.fromString(historyId))
        return ServerResponse.noContent().buildAndAwait()
    }
}
