package com.reactive.restdemo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class TestConfiguration {

    @Bean
    open fun testGateway(restTemplate: RestTemplate) = TestGateway(restTemplate)

}