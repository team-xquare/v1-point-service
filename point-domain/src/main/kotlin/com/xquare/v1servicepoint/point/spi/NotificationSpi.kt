package com.xquare.v1servicepoint.point.spi

import com.xquare.v1servicepoint.annotation.Spi
import java.util.*

@Spi
interface NotificationSpi {
    suspend fun sendNotification(userId: UUID, topic: String, content: String, threadId: String)
}
