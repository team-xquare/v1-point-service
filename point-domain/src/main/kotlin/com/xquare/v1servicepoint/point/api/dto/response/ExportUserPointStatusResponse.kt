package com.xquare.v1servicepoint.point.api.dto.response

data class ExportUserPointStatusResponse(
    val fileName: String,
    val file: ByteArray,
)
