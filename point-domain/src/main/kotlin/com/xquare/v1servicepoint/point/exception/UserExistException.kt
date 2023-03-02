package com.xquare.v1servicepoint.point.exception

import com.xquare.v1servicepoint.exception.BaseException

class UserExistException(
    message: String,
) : BaseException(message, 409) {
    companion object {
        const val USER_ID_EXIST = "User Id Exist"
    }
}
