package com.reactive.restdemo

import com.reactive.restdemo.Application.Item
import reactor.core.publisher.Flux

interface AsyncRestGateway {
    fun get(id: Int): Flux<Result>
    fun insert(item: Item): Flux<Result>
    fun final(item: Item): Flux<Result>

    data class Response(
        val result: String,
        val item: Int,
        val error: String?
    )

    sealed class Result{
        data class Failure(val error: String): Result()
        data class Success(val item: Int): Result()
    }
}

