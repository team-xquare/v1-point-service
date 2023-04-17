package com.xquare.v1servicepoint.point.spi.point

import com.xquare.v1servicepoint.annotation.Spi
import com.xquare.v1servicepoint.point.Point
import java.util.UUID

@Spi
interface QueryPointSpi {
    suspend fun findByPointId(pointId: UUID): Point?

    suspend fun findAllByType(type: Boolean): List<Point>

    suspend fun findAllByReason(reason: String): List<Point>
}
