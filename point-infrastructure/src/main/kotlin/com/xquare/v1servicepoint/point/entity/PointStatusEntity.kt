package com.xquare.v1servicepoint.point.entity

import com.github.f4b6a3.uuid.UuidCreator
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tbl_point_status")
class PointStatusEntity(

    @Id
    @Column(columnDefinition = "BINARY(16)", nullable = false)
    val userId: UUID = UuidCreator.getTimeOrderedEpoch(),

    goodPoint: Int,

    badPoint: Int,

    penaltyLevel: Int,

    isPenaltyRequired: Boolean
) {

    @field:Column(columnDefinition = "INT", nullable = false)
    var goodPoint = goodPoint
        protected set

    @field:Column(columnDefinition = "INT", nullable = false)
    var badPoint = badPoint
        protected set

    @field:Column(columnDefinition = "INT DEFAULT 1", nullable = false)
    var penaltyLevel = penaltyLevel
        protected set

    @field:Column(columnDefinition = "TINYINT(1)", nullable = false)
    var isPenaltyRequired = isPenaltyRequired
        protected set
}
