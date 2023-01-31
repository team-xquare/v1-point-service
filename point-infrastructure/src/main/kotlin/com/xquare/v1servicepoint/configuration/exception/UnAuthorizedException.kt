package com.xquare.v1servicepoint.configuration.exception

import com.xquare.v1servicepoint.exception.BaseException

class UnAuthorizedException(
    message: String
) : BaseException(message, 401)
