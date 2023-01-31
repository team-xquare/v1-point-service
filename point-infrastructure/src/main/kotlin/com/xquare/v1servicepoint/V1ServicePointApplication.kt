package com.xquare.v1servicepoint

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

internal const val BASE_PACKAGE = "com.xquare.v1servicepoint"

@SpringBootApplication
class V1ServicePointApplication

fun main(args: Array<String>) {
    runApplication<V1ServicePointApplication>(*args)
}
