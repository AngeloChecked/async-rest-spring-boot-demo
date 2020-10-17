package com.reactive.restdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient


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
	open fun useCase(restGateway: AsyncRestGateway): UseCase = UseCase(restGateway)

	@Bean
	open fun restTemplate(): RestTemplate = RestTemplateBuilder().build()

	@Bean
	open fun webClient(): WebClient = WebClient.create()

	@Bean
	open fun gateway(webClient: WebClient): AsyncRestGateway = AsyncGenericRestGateway(webClient)
}


