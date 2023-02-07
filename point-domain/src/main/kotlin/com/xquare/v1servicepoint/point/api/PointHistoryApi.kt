package com.xquare.v1servicepoint.point.api

import com.xquare.v1servicepoint.point.api.dto.request.DomainGivePointUserRequest
import java.util.UUID

interface PointHistoryApi {
    suspend fun saveUserPoint(userId: UUID, givePointUserRequest: DomainGivePointUserRequest)
}
