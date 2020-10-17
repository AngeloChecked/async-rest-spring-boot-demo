package com.reactive.restdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate


@SpringBootApplication
class Application {
	data class Item(val id: Int)
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}

@Configuration
class SpringbootConfig{
	@Bean
	open fun useCase(restGateway: RestGateway): UseCase = UseCase(restGateway)

	@Bean
	open fun restTemplate(): RestTemplate = RestTemplateBuilder().build()

	@Bean
	open fun gateway(restTemplate: RestTemplate): RestGateway = GenericRestGateway(restTemplate)
}


