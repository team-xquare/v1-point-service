package com.xquare.v1servicepoint.point.api

import com.xquare.v1servicepoint.point.api.dto.request.DomainGivePointUserRequest
import com.xquare.v1servicepoint.point.api.dto.response.PointHistoryListResponse
import java.util.UUID

interface PointHistoryApi {
    suspend fun saveUserPoint(userId: UUID, givePointUserRequest: DomainGivePointUserRequest)

    suspend fun deleteUserPoint(studentId: UUID, historyId: UUID)

    suspend fun queryUserPointHistory(userId: UUID, type: Boolean): PointHistoryListResponse
}
