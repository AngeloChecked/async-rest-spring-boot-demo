package com.reactive.restdemo

import com.reactive.restdemo.Application.Item
import reactor.core.publisher.Mono

interface AsyncRestGateway {
    fun get(id: Int): Mono<Result>
    fun insert(item: Item): Mono<Result>
    fun final(item: Item): Mono<Result>

    data class Response(
        val result: String,
        val item: Int,
        val error: String? = ""
    )

    sealed class Result{
        data class Failure(val type: String, val error: String): Result()
        data class Success(val type: String, val item: Int): Result()
    }
}

