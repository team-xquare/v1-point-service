package com.xquare.v1servicepoint.point.spi.point

import com.xquare.v1servicepoint.annotation.Spi
import com.xquare.v1servicepoint.point.Point
import java.util.UUID

@Spi
interface CommandPointSpi {

    suspend fun applyPointChanges(point: Point): Point

    suspend fun deletePointRole(pointId: UUID)

    suspend fun savePointRole(point: Point)
}
