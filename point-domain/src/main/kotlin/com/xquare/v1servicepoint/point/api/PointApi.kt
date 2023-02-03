package com.xquare.v1servicepoint.point.api

import com.xquare.v1servicepoint.point.api.dto.response.PointStatusResponse
import java.util.UUID

interface PointApi {

    suspend fun queryPointStatus(userId: UUID): PointStatusResponse
}
