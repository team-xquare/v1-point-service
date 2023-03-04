package com.xquare.v1servicepoint.point.exception

import com.xquare.v1servicepoint.exception.BaseException

class UserRequsetFailedException(
    message: String,
    statusCode: Int,
) : BaseException(message, statusCode)
