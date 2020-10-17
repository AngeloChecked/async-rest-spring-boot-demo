package com.reactive.restdemo

import com.reactive.restdemo.AsyncRestGateway.Result
import com.reactive.restdemo.Application.Item
import com.reactive.restdemo.AsyncRestGateway.Response
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class AsyncGenericRestGateway(
    private val webClient: WebClient
) : AsyncRestGateway {
    override fun get(id: Int): Flux<Result> {
        logger.info("starting insert request for itemId $id")
        return try {
            val result = webClient
                .get()
                .uri("$HOST_NAME/getSomething/$id")
                .retrieve()
                .bodyToFlux(Response::class.java)

            mapAppResults(result)
        } catch (e: Throwable) {
            Flux.create { it.next(Result.Failure(error = e.localizedMessage)).complete() }
        }
    }

    override fun insert(item: Item): Flux<Result> {
        logger.info("starting insert request for item $item")
        return try {
            val result = webClient
                .post()
                .uri("$HOST_NAME/insertSomething")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item::class.java)
                .retrieve()
                .bodyToFlux(Response::class.java)
            mapAppResults(result)
        } catch (e: Throwable) {
            Flux.create { it.next(Result.Failure(error = e.localizedMessage)).complete() }
        }
    }

    override fun final(item: Item): Flux<Result> {
        logger.info("starting final request for item $item")
        return try {
            val result = webClient
                .post()
                .uri("$HOST_NAME/finalRequest")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item::class.java)
                .retrieve()
                .bodyToFlux(Response::class.java)
            mapAppResults(result)
        } catch (e: Throwable) {
            Flux.create { it.next(Result.Failure(error = e.localizedMessage)).complete() }
        }
    }

    private fun mapAppResults(result: Flux<Response>): Flux<Result> {
        return Flux.create {
            result.subscribe { body ->
                logger.info("the request response: $body")
                it.next(when (body.result) {
                    "OK" -> Result.Success(item = body.item)
                    "KO" -> Result.Failure(error = body.error ?: "missing server error")
                    else -> Result.Failure(error = "unhandled response: $body")
                }).complete()
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UseCase::class.java)
        private const val HOST_NAME = "http://localhost:8080"
    }
}