package com.xquare.v1servicepoint.point.spi.point

import com.xquare.v1servicepoint.annotation.Spi

@Spi
interface PointSpi : QueryPointSpi, CommandPointSpi
