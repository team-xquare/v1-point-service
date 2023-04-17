package com.xquare.v1servicepoint.point.api

import com.xquare.v1servicepoint.point.api.dto.request.DomainSavePointRoleRequest
import com.xquare.v1servicepoint.point.api.dto.request.DomainUpdatePointRoleRequest
import com.xquare.v1servicepoint.point.api.dto.response.PointRuleListResponse
import java.util.UUID

interface PointApi {
    suspend fun updatePointRole(pointId: UUID, request: DomainUpdatePointRoleRequest)

    suspend fun deletePointRole(pointId: UUID)

    suspend fun savePointRole(request: DomainSavePointRoleRequest)

    suspend fun queryPointRoleList(type: Boolean): PointRuleListResponse
}
