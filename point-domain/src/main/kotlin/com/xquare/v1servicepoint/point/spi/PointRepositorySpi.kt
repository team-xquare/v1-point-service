package com.xquare.v1servicepoint.point.spi

import com.xquare.v1servicepoint.annotation.Spi
import com.xquare.v1servicepoint.point.Point
import java.util.UUID

@Spi
interface PointRepositorySpi {
    suspend fun findByPointId(pointId: UUID): Point?

    suspend fun applyPointChanges(point: Point): Point

  suspend fun savePointRole(point: Point)
}
