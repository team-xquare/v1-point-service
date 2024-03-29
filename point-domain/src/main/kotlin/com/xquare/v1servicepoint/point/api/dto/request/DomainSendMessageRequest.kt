package com.xquare.v1servicepoint.point.api.dto.request

import java.util.UUID

data class DomainSendMessageRequest(
    val userId: UUID,
    val topic: String,
    val content: String,
    val threadId: String,
)
