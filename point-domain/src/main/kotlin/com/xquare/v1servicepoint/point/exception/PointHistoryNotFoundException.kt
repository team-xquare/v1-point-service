package com.xquare.v1servicepoint.point.exception

import com.xquare.v1servicepoint.exception.BaseException

class PointHistoryNotFoundException(
    message: String,
) : BaseException(message, 404) {
    companion object {
        const val POINT_HISTORY_NOT_FOUND = "Point History Not Found"
    }
}
