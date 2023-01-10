package com.xquare.v1servicepoint.point.entity

import com.github.f4b6a3.uuid.UuidCreator
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tbl_point")
class PointEntity(

    @Id
    @Column(columnDefinition = "BINARY(16)", nullable = false)
    val id: UUID = UuidCreator.getTimeOrderedEpoch(),

    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    val reason: String,

    @Column(nullable = false)
    val point: Int,

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    val type: Int
)
