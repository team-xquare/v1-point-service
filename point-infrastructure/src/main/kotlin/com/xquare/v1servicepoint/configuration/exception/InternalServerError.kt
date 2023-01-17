package com.xquare.v1servicepoint.configuration.exception

import com.xquare.v1servicepoint.exception.BaseException

class InternalServerError(
    errorMessage: String
) : BaseException(errorMessage, 500) {
    companion object {
        const val UNEXPECTED_EXCEPTION = "Unexpected Error Occurred"
    }
}