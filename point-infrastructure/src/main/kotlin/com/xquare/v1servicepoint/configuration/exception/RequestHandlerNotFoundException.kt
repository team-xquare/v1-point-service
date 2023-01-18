package com.xquare.v1servicepoint.configuration.exception

import com.xquare.v1servicepoint.exception.BaseException

class RequestHandlerNotFoundException(
    errorMessage: String
) : BaseException(errorMessage, 404)
