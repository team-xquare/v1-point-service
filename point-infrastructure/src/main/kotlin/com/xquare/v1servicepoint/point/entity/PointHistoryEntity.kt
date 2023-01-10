package com.xquare.v1servicepoint.point.entity

import com.github.f4b6a3.uuid.UuidCreator
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "tbl_point_history")
class PointHistoryEntity(

    @Id
    @Column(columnDefinition = "BINARY(16)", nullable = false)
    val id: UUID = UuidCreator.getTimeOrderedEpoch(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id", nullable = false)
    val point: PointEntity,

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    val userId: UUID
)
