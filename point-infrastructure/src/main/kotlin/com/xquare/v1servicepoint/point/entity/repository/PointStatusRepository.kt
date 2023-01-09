package com.xquare.v1servicepoint.point.entity.repository

import com.xquare.v1servicepoint.point.entity.PointStatusEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PointStatusRepository : CoroutineCrudRepository<PointStatusEntity, UUID>
