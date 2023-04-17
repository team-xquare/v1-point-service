package com.xquare.v1servicepoint.point.spi.pointhistory

import com.xquare.v1servicepoint.annotation.Spi

@Spi
interface PointHistorySpi : QueryPointHistorySpi, CommandPointHistorySpi
