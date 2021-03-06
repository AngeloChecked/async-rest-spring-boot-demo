package com.reactive.restdemo.config

import com.reactive.restdemo.AsyncRestGateway
import com.reactive.restdemo.Application.Item
import org.springframework.web.client.RestTemplate

class TestGateway (
    private val restTemplate: RestTemplate
){
    fun sendInit(item: Item) {
            restTemplate.postForEntity(
                "$HOST_NAME/initRequest", item, AsyncRestGateway.Response::class.java
            )
    }

    companion object {
        private const val HOST_NAME = "http://localhost:8080"
    }
}