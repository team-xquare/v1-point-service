package com.xquare.v1servicepoint.point.exception

import com.xquare.v1servicepoint.exception.BaseException

class UserPenaltyExistException(
    message: String,
) : BaseException(message, 409) {

    companion object {
        const val USER_PENALTY_EXIST = "User Penalty Exist"
    }
}
