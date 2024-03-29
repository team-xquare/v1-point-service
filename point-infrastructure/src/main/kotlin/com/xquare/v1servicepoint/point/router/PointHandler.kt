package com.xquare.v1servicepoint.point.router

import com.xquare.v1servicepoint.configuration.exception.UnAuthorizedException
import com.xquare.v1servicepoint.point.api.PointApi
import com.xquare.v1servicepoint.point.api.PointHistoryApi
import com.xquare.v1servicepoint.point.api.PointStatusApi
import com.xquare.v1servicepoint.point.api.dto.request.DomainGivePointUserRequest
import com.xquare.v1servicepoint.point.api.dto.request.DomainSavePointRoleRequest
import com.xquare.v1servicepoint.point.api.dto.request.DomainUpdatePointRoleRequest
import com.xquare.v1servicepoint.point.router.dto.SavePointRoleRequest
import com.xquare.v1servicepoint.point.router.dto.SaveUserPointRequest
import com.xquare.v1servicepoint.point.router.dto.UpdatePointRoleRequest
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
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
    private val pointStatusApi: PointStatusApi,
) {
    suspend fun queryUserPointStatus(serverRequest: ServerRequest): ServerResponse {
        val userId = serverRequest.pathVariable("student-id")

        val pointStatus = pointStatusApi.queryUserPointStatus(UUID.fromString(userId))
        return ServerResponse.ok().bodyValueAndAwait(pointStatus)
    }

    suspend fun saveUserPoint(serverRequest: ServerRequest): ServerResponse {
        val userId = serverRequest.pathVariable("student-id")

        val savePointUserRequest = serverRequest.getSavePointRequestBody()
        val domainRequest = savePointUserRequest.toDomainRequest()

        pointHistoryApi.saveUserPoint(UUID.fromString(userId), domainRequest)
        return ServerResponse.created(URI("/points/student")).buildAndAwait()
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

    suspend fun queryUserPointHistory(serverRequest: ServerRequest): ServerResponse {
        val userId = serverRequest.pathVariable("student-id")
        val type = serverRequest.queryParam("type").orElse("")

        val pointHistoryListResponse = pointHistoryApi.queryUserPointHistory(UUID.fromString(userId), type)
        return ServerResponse.ok().bodyValueAndAwait(pointHistoryListResponse)
    }

    suspend fun queryUserPointHistoryForStudent(serverRequest: ServerRequest): ServerResponse {
        val userId = serverRequest.headers().firstHeader("Request-User-Id")
            ?: throw UnAuthorizedException("UnAuthorized")
        val type = serverRequest.queryParam("type").orElse("")

        val pointHistoryListForStudentResponse =
            pointHistoryApi.queryUserPointHistoryForStudent(UUID.fromString(userId), type)
        return ServerResponse.ok().bodyValueAndAwait(pointHistoryListForStudentResponse)
    }

    suspend fun updatePointRole(serverRequest: ServerRequest): ServerResponse {
        val pointId = serverRequest.pathVariable("point-id")
        val updatePointRoleRequest = serverRequest.getUpdatePointRequestBody()
        val domainRequest = updatePointRoleRequest.toDomainRequest()

        pointApi.updatePointRole(UUID.fromString(pointId), domainRequest)
        return ServerResponse.noContent().buildAndAwait()
    }

    private suspend fun ServerRequest.getUpdatePointRequestBody() =
        this.bodyToMono<UpdatePointRoleRequest>().awaitSingle()

    private fun UpdatePointRoleRequest.toDomainRequest() = DomainUpdatePointRoleRequest(
        reason = this.reason,
        type = this.type,
        point = this.point,
    )

    suspend fun deletePointRole(serverRequest: ServerRequest): ServerResponse {
        val pointId = serverRequest.pathVariable("point-id")
        pointApi.deletePointRole(UUID.fromString(pointId))
        return ServerResponse.noContent().buildAndAwait()
    }

    suspend fun savePointRole(serverRequest: ServerRequest): ServerResponse {
        val savePointRoleRequest = serverRequest.getSavePointRoleRequestBody()
        val domainRequest = savePointRoleRequest.toDomainRequest()

        pointApi.savePointRole(domainRequest)
        return ServerResponse.created(URI("/points/rule")).buildAndAwait()
    }

    private suspend fun ServerRequest.getSavePointRoleRequestBody() =
        this.bodyToMono<SavePointRoleRequest>().awaitSingle()

    private fun SavePointRoleRequest.toDomainRequest() = DomainSavePointRoleRequest(
        reason = this.reason,
        type = this.type,
        point = this.point,
    )

    suspend fun queryPointRuleList(serverRequest: ServerRequest): ServerResponse {
        val type = serverRequest.queryParam("type").orElse("")

        val pointRuleListResponse = pointApi.queryPointRoleList(type.toBoolean())
        return ServerResponse.ok().bodyValueAndAwait(pointRuleListResponse)
    }

    suspend fun savePointStatus(serverRequest: ServerRequest): ServerResponse {
        val userId = serverRequest.pathVariable("student-id")

        pointStatusApi.savePointStatus(UUID.fromString(userId))
        return ServerResponse.created(URI("/")).buildAndAwait()
    }

    suspend fun queryUserPointStatusExcel(serverRequest: ServerRequest): ServerResponse {
        val response = pointHistoryApi.queryUserPointHistoryExcel()
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${response.fileName}")
            .bodyValueAndAwait(ByteArrayResource(response.file))
    }

    suspend fun saveUserPenaltyEducationComplete(serverRequest: ServerRequest): ServerResponse {
        val userId = serverRequest.pathVariable("student-id")

        pointStatusApi.saveUserPenaltyEducationComplete(UUID.fromString(userId))
        return ServerResponse.noContent().buildAndAwait()
    }

    suspend fun queryStudentStatus(serverRequest: ServerRequest): ServerResponse {
        val name = serverRequest.queryParams().getFirst("name")
        val penaltyLevel = serverRequest.queryParams().getFirst("penaltyLevel")?.toIntOrNull()

        val pointStatus = pointStatusApi.queryStudentStatus(name, penaltyLevel)
        return ServerResponse.ok().bodyValueAndAwait(pointStatus)
    }
}
