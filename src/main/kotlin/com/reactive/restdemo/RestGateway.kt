package com.reactive.restdemo

import com.reactive.restdemo.Application.Item

interface RestGateway {
    fun get(id: Int): Result
    fun insert(item: Item): Result
    fun final(item: Item): Result

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

