package com.xquare.v1servicepoint.configuration.webclient

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties(prefix = "webclient.services")
data class WebClientProperties(
    var name: String = "webClient",
    var url: String = "",
    var maxConnections: Int? = null,
    var connectionTimeout: Int = 3000,
    var readTimeout: Long = 3000L,
    var writeTimeout: Long = 9000L,
    var maxIdleTime: Duration? = null,
    var maxRetry: Long = 3,
    var retryDelay: Duration = Duration.ofSeconds(1)
) {
    fun overrideProperties(name: String, other: WebClientProperties) {
        this.name = name
        if (maxConnections != other.maxConnections) {
            maxConnections = other.maxConnections
        }
        if (connectionTimeout != other.connectionTimeout) {
            connectionTimeout = other.connectionTimeout
        }
        if (readTimeout != other.readTimeout) {
            readTimeout = other.readTimeout
        }
        if (writeTimeout != other.writeTimeout) {
            writeTimeout = other.writeTimeout
        }
        if (maxIdleTime != other.maxIdleTime) {
            maxIdleTime = other.maxIdleTime
        }
        if (maxRetry != other.maxRetry) {
            maxRetry = other.maxRetry
        }
        if (retryDelay != other.retryDelay) {
            retryDelay = other.retryDelay
        }
    }
}
