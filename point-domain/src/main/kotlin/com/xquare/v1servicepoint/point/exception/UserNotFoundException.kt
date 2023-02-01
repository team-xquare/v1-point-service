package com.xquare.v1servicepoint.point.exception

import com.xquare.v1servicepoint.exception.BaseException

class UserNotFoundException(
    message: String,
) : BaseException(message, 404) {
    companion object {
        const val USER_ID_NOT_FOUND = "User ID is not found even if you are select for it"
    }
}
