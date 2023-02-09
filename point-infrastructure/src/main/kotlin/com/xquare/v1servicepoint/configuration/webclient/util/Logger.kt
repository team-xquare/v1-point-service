package com.xquare.v1servicepoint.configuration.webclient.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory


inline fun <reified T> T.logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}