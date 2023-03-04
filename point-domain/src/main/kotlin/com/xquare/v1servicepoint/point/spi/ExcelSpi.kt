package com.xquare.v1servicepoint.point.spi

import com.xquare.v1servicepoint.annotation.Spi

@Spi
interface ExcelSpi {

    suspend fun writeUserPointHistoryExcelFile(): ByteArray
}
