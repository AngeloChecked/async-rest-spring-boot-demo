package com.reactive.restdemo

import com.reactive.restdemo.RestGateway.Result
import com.reactive.restdemo.Application.Item
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

class GenericRestGateway(
    private val restTemplate: RestTemplate
) : RestGateway {
    override fun get(id: Int): Result {
        return try {
            val result = restTemplate.getForEntity(
                "$HOST_NAME/getSomething/$id", RestGateway.Response::class.java
            )
            mapAppResults(result)
        } catch (e: Throwable) {
            Result.Failure(error = e.localizedMessage)
        }
    }

    override fun insert(item: Item): Result {
        return try {
            val result = restTemplate.postForEntity(
                "$HOST_NAME/insertSomething", item, RestGateway.Response::class.java
            )
            mapAppResults(result)
        } catch (e: Throwable) {
            Result.Failure(error = e.localizedMessage)
        }
    }

    override fun final(item: Item): Result {
        return try {
            val result = restTemplate.postForEntity(
                "$HOST_NAME/finalRequest", item, RestGateway.Response::class.java
            )
            mapAppResults(result)
        } catch (e: Throwable) {
            Result.Failure(error = e.localizedMessage)
        }
    }

    private fun mapAppResults(result: ResponseEntity<RestGateway.Response>): Result {
        val body = result.body
        return when (body?.result) {
            "OK" -> Result.Success(item = body.item)
            "KO" -> Result.Failure(error = body.error ?: "missing server error")
            else -> Result.Failure(error = "unhandled response: $body")
        }
    }

    companion object {
        private const val HOST_NAME = "http://localhost:8080"
    }
}