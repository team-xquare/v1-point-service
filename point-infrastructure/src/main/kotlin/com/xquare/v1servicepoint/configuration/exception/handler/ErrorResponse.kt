package com.xquare.v1servicepoint.configuration.exception.handler

data class ErrorResponse(
    val errorMessage: String?,
    val responseStatus: Int
)
