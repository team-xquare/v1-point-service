package com.xquare.v1servicepoint.point.api

import com.xquare.v1servicepoint.point.api.dto.request.DomainGivePointUserRequest
import com.xquare.v1servicepoint.point.api.dto.response.ExportUserPointStatusResponse
import com.xquare.v1servicepoint.point.api.dto.response.PointHistoryListResponse
import com.xquare.v1servicepoint.point.api.dto.response.PointHistoryListStudentResponse
import java.util.UUID

interface PointHistoryApi {
    suspend fun saveUserPoint(userId: UUID, givePointUserRequest: DomainGivePointUserRequest)

    suspend fun deleteUserPoint(studentId: UUID, historyId: UUID)

    suspend fun queryUserPointHistory(userId: UUID, type: String): PointHistoryListResponse

    suspend fun queryUserPointHistoryForStudent(userId: UUID, type: String): PointHistoryListStudentResponse

    suspend fun savePointStatus(userId: UUID)

    suspend fun queryUserPointHistoryExcel(): ExportUserPointStatusResponse

    suspend fun saveUserPenaltyEducationComplete(userId: UUID)
}
