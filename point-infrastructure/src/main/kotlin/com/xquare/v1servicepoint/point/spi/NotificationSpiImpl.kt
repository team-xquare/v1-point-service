package com.xquare.v1servicepoint.point.spi

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.MessageAttributeValue
import com.amazonaws.services.sqs.model.SendMessageRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.xquare.v1servicepoint.point.api.dto.request.DomainSendMessageRequest
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class NotificationSpiImpl(
    private val objectMapper: ObjectMapper,
    private val amazonSQS: AmazonSQS,
) : NotificationSpi {

    companion object {
        const val NOTIFICATION_FIFO = "notification.fifo"
    }

    override suspend fun sendNotification(userId: UUID, topic: String, content: String, threadId: String) {
        val domainSendMessageRequest = DomainSendMessageRequest(
            userId = userId,
            topic = topic,
            content = content,
            threadId = threadId,
        )

        val sendMessageRequest = SendMessageRequest(
            amazonSQS.getQueueUrl(NOTIFICATION_FIFO).queueUrl,
            objectMapper.writeValueAsString(domainSendMessageRequest),
        )
            .withMessageGroupId("point")
            .withMessageDeduplicationId(UUID.randomUUID().toString())
            .withMessageAttributes(
                mapOf(
                    "Content-Type" to MessageAttributeValue()
                        .withDataType("String")
                        .withStringValue("application/json")
                ),
            )
        amazonSQS.sendMessage(sendMessageRequest)
    }
}
