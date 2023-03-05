package com.xquare.v1servicepoint.point.exception

import com.xquare.v1servicepoint.exception.BaseException

class PointStatusNotFoundException(
    message: String,
) : BaseException(message, 404) {
    companion object {
        const val POINT_STATUS_NOT_FOUND = "Point Status Not Found"
    }
}
