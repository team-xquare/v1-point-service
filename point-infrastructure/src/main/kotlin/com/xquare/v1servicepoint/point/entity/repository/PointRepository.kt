package com.xquare.v1servicepoint.point.entity.repository

import com.xquare.v1servicepoint.point.entity.PointEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PointRepository : CoroutineCrudRepository<PointEntity, UUID>
