package com.xquare.v1servicepoint.point.spi.pointstatus

import com.xquare.v1servicepoint.annotation.Spi

@Spi
interface PointStatusSpi : QueryPointStatusSpi, CommandPointStatusSpi
