package com.xquare.v1servicepoint.configuration.exception

import com.xquare.v1servicepoint.exception.BaseException

class BadRequestException(
    errormessage: String
) : BaseException(errormessage, 400)
