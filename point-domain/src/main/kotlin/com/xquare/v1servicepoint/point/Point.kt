package com.xquare.v1servicepoint.point

import com.xquare.v1servicepoint.annotation.Aggregate
import java.util.UUID

@Aggregate
data class Point(
    val id: UUID,

    val reason: String,

    val point: Int,

    val type: Boolean, //TODO 컬럼 이름 바꾸기
) {
    fun updatePointRole(reason: String, point: Int, type: Boolean): Point {
        return copy(
            reason = reason,
            point = point,
            type = type,
        )
    }
}
