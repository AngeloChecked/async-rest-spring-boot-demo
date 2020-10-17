package com.reactive.restdemo

import com.reactive.restdemo.AsyncRestGateway.Result
import com.reactive.restdemo.Application.Item
import com.reactive.restdemo.AsyncGenericRestGateway.ApplicativeFailureException.*
import com.reactive.restdemo.AsyncRestGateway.Response
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.Exception
import kotlin.reflect.jvm.javaConstructor

class AsyncGenericRestGateway(
    private val webClient: WebClient
) : AsyncRestGateway {
    override fun get(id: Int): Mono<Result> {
        logger.info("starting insert request for itemId $id")
        return try {
            val result = webClient
                .get()
                .uri("$HOST_NAME/getSomething/$id")
                .retrieve()
                .bodyToMono(Response::class.java)

            mapAppResults(result,"get") { err -> GetException(err) }
        } catch (e: Throwable) {
            Mono.create { it.error(e) }
        }
    }

    override fun insert(item: Item): Mono<Result> {
        logger.info("starting insert request for item $item")
        return try {
            val result = webClient
                .post()
                .uri("$HOST_NAME/insertSomething")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item::class.java)
                .retrieve()
                .bodyToMono(Response::class.java)
            mapAppResults(result,"insert") { err -> InsertException(err) }
        } catch (e: Throwable) {
            Mono.create { it.error(e) }
        }
    }

    override fun final(item: Item): Mono<Result> {
        logger.info("starting final request for item $item")
        return try {
            val result = webClient
                .post()
                .uri("$HOST_NAME/finalRequest")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item::class.java)
                .retrieve()
                .bodyToMono(Response::class.java)
            mapAppResults(result,"final") { err -> FinalException(err) }
        } catch (e: Throwable) {
            Mono.create { it.error(e) }
        }
    }

    private fun mapAppResults(result: Mono<Response>, type: String, error: (Result.Failure) -> (ApplicativeFailureException)): Mono<Result> {
        return Mono.create {
            result.subscribe { body ->
                logger.info("the request response: $body")
                when (body.result) {
                    "OK" -> it.success(Result.Success(type, item = body.item))
                    "KO" -> it.error(error(Result.Failure(type, body.error ?: "server missing error")))
                    else -> it.error(error(Result.Failure(type, body.error ?: "server missing error")))
                }
            }
        }
    }

    sealed class ApplicativeFailureException() : Exception() {
        data class GetException(val error: Result.Failure): ApplicativeFailureException()
        data class InsertException(val error: Result.Failure): ApplicativeFailureException()
        data class FinalException(val error: Result.Failure): ApplicativeFailureException()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UseCase::class.java)
        private const val HOST_NAME = "http://localhost:8080"
    }
}