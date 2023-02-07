package com.xquare.v1servicepoint.point.exception

import com.xquare.v1servicepoint.exception.BaseException

class PointNotFoundException(
    message: String,
) : BaseException(message, 404) {
    companion object {
        const val POINT_NOT_FOUND = "Point Not Found"
    }
}
