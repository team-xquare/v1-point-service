package com.xquare.v1servicepoint.configuration.webclient

import org.springframework.web.reactive.function.client.WebClient

class WebClientPair(
    val webClient: WebClient,
    val properties: WebClientProperties
)