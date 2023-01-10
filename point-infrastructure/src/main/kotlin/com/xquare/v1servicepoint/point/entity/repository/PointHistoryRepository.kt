package com.xquare.v1servicepoint.point.entity.repository

import com.xquare.v1servicepoint.point.entity.PointHistoryEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PointHistoryRepository : CoroutineCrudRepository<PointHistoryEntity, UUID>
