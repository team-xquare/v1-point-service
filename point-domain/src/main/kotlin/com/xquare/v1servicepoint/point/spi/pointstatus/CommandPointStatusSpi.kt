package com.xquare.v1servicepoint.point.spi.pointstatus

import com.xquare.v1servicepoint.annotation.Spi
import com.xquare.v1servicepoint.point.PointStatus

@Spi
interface CommandPointStatusSpi {
    suspend fun savePointStatus(pointStatus: PointStatus)

    suspend fun applyPointStatusChanges(pointStatus: PointStatus): PointStatus
}
