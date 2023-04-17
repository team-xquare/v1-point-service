package com.xquare.v1servicepoint.point.api

import com.xquare.v1servicepoint.point.api.dto.response.PointStatusResponse
import com.xquare.v1servicepoint.point.api.dto.response.PointStudentStatusResponse
import java.util.UUID

interface PointStatusApi {

    suspend fun saveUserPenaltyEducationComplete(userId: UUID)

    suspend fun savePointStatus(userId: UUID)

    suspend fun queryUserPointStatus(userId: UUID): PointStatusResponse

    suspend fun queryStudentStatus(name: String?, penaltyLevel: Int?): PointStudentStatusResponse
}
