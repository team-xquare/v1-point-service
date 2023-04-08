package com.xquare.v1servicepoint.point.api.impl

import com.xquare.v1servicepoint.annotation.UseCase
import com.xquare.v1servicepoint.point.PointStatus
import com.xquare.v1servicepoint.point.api.PointStatusApi
import com.xquare.v1servicepoint.point.api.dto.response.PointStatusResponse
import com.xquare.v1servicepoint.point.api.dto.response.PointStudentStatusElement
import com.xquare.v1servicepoint.point.api.dto.response.PointStudentStatusResponse
import com.xquare.v1servicepoint.point.exception.PointStatusNotFoundException
import com.xquare.v1servicepoint.point.exception.UserExistException
import com.xquare.v1servicepoint.point.exception.UserNotFoundException
import com.xquare.v1servicepoint.point.exception.UserPenaltyExistException
import com.xquare.v1servicepoint.point.spi.UserSpi
import com.xquare.v1servicepoint.point.spi.point.QueryPointSpi
import com.xquare.v1servicepoint.point.spi.pointhistory.CommandPointHistorySpi
import com.xquare.v1servicepoint.point.spi.pointstatus.CommandPointStatusSpi
import com.xquare.v1servicepoint.point.spi.pointstatus.QueryPointStatusSpi
import java.util.UUID

@UseCase
class PointStatusApiImpl(
    private val queryPointSpi: QueryPointSpi,
    private val queryPointStatusSpi: QueryPointStatusSpi,
    private val commandPointStatusSpi: CommandPointStatusSpi,
    private val commandPointHistorySpi: CommandPointHistorySpi,
    private val userSpi: UserSpi,
    ) : PointStatusApi {

    override suspend fun saveUserPenaltyEducationComplete(userId: UUID) {
        val userPointStatus = queryPointStatusSpi.findByUserId(userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        if (!userPointStatus.isPenaltyRequired) {
            throw UserPenaltyExistException(UserPenaltyExistException.USER_PENALTY_EXIST)
        }

        val point = queryPointSpi.findAllByReason(PointHistoryApiImpl.POINT_REASON)

        if (userPointStatus.penaltyLevel >= 4) {
            val penaltyEducationComplete = applyOutPenaltyStatusChange(userPointStatus)
            val penaltyStart = calculatePenaltyStart(penaltyEducationComplete)
            commandPointStatusSpi.applyPointStatusChanges(penaltyStart)
            commandPointHistorySpi.saveUserListPointHistory(userId, point)
            return
        }

        val penaltyEducationComplete = applyPenaltyStatusChanges(userPointStatus)
        val penaltyStart = calculatePenaltyStart(penaltyEducationComplete)

        commandPointStatusSpi.applyPointStatusChanges(penaltyStart)
        commandPointHistorySpi.saveUserListPointHistory(userId, point)
    }


    private fun calculatePenaltyStart(penaltyEducationComplete: PointStatus): PointStatus {
        val penaltyLevelUp = penaltyEducationComplete.penaltyLevelUp()
        return if (penaltyEducationComplete.badPoint >= PointHistoryApiImpl.PENALTY_LEVEL_LIST[penaltyEducationComplete.penaltyLevel]) {
            penaltyLevelUp.penaltyEducationStart()
        } else {
            penaltyEducationComplete.penaltyEducationComplete()
        }
    }

    private fun applyPenaltyStatusChanges(pointStatus: PointStatus): PointStatus {
        val pointStatusMinusGoodPoint = pointStatus.penaltyEducationCompleteAndMinusGoodPoint()
        val pointStatusMinusBadPoint = pointStatusMinusGoodPoint.penaltyEducationCompleteAndMinusBadPoint()
        return pointStatusMinusBadPoint.penaltyEducationComplete()
    }

    private fun applyOutPenaltyStatusChange(pointStatus: PointStatus): PointStatus {
        val pointStatusMinusBadPoint = pointStatus.penaltyEducationCompleteAndMinusBadPoint()
        return pointStatusMinusBadPoint.penaltyEducationComplete()
    }

    override suspend fun savePointStatus(userId: UUID) {
        queryPointStatusSpi.findByUserId(userId)?.run {
            throw UserExistException(UserExistException.USER_ID_EXIST)
        }

        val pointStatus = PointStatus(
            userId = userId,
            goodPoint = 0,
            badPoint = 0,
            penaltyLevel = 1,
            isPenaltyRequired = false,
        )
        commandPointStatusSpi.savePointStatus(pointStatus)
    }

    override suspend fun queryUserPointStatus(userId: UUID): PointStatusResponse {
        val getUserPointStatus = queryPointStatusSpi.findByUserId(userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        return PointStatusResponse(
            goodPoint = getUserPointStatus.goodPoint,
            badPoint = getUserPointStatus.badPoint,
        )
    }

    override suspend fun queryStudentStatus(name: String?, penaltyLevel: Int?): PointStudentStatusResponse {
        val pointStatusList = queryPointStatusSpi.findAllByPenaltyLevel(penaltyLevel)
        val users = userSpi.getStudent()
        val students = users
            .filter {
                name?.let { name -> it.name.contains(name) } ?: true
            }.map {
                val status = pointStatusList.find { pointStatus -> pointStatus.userId == it.id }
                    ?: throw PointStatusNotFoundException(PointStatusNotFoundException.POINT_STATUS_NOT_FOUND)

                PointStudentStatusElement(
                    id = it.id,
                    name = it.name,
                    num = "${it.grade}${it.classNum}${it.num.toString().padStart(2, '0')}",
                    goodPoint = status.goodPoint,
                    badPoint = status.badPoint,
                    penaltyLevel = status.penaltyLevel,
                    isPenaltyRequired = status.isPenaltyRequired,
                )
            }.sortedBy { it.num }

        return PointStudentStatusResponse(students)
    }
}
