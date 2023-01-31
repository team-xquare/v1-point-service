package com.xquare.v1servicepoint.point.api.impl

import com.xquare.v1servicepoint.annotation.UseCase
import com.xquare.v1servicepoint.point.api.PointApi
import com.xquare.v1servicepoint.point.api.dto.response.PointStatusResponse
import com.xquare.v1servicepoint.point.exception.UserNotFoundException
import com.xquare.v1servicepoint.point.spi.PointStatusSpi
import java.util.UUID

@UseCase
class PointApiImpl(
    private val pointStatusSpi: PointStatusSpi,
) : PointApi {

    override suspend fun queryPointStatus(userId: UUID): PointStatusResponse {
        val getUserPointStatus = pointStatusSpi.findByUserId(userId)
            ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

        return PointStatusResponse(
            goodPoint = getUserPointStatus.goodPoint,
            badPoint = getUserPointStatus.badPoint
        )
    }
}
