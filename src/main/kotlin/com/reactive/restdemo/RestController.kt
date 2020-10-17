package com.reactive.restdemo

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

@RestController
class RestController(
    val useCase: UseCase
) {
    val random = Random(1)

    @PostMapping("initRequest", consumes = ["application/json"])
    fun initRequest(@RequestBody request: Application.Item): ResponseEntity<HttpStatus> {
        useCase.execute(request)
        return ResponseEntity(HttpStatus.CREATED)
    }

    @PostMapping("finalRequest", consumes = ["application/json"])
    fun finalRequest(@RequestBody request: Application.Item): ResponseEntity<String> {
        return if (random.nextInt() % 2 != 0)
            ResponseEntity("""{"result":"KO", "item": ${request.id}, "error":"error occurred in the final request"}""", HttpStatus.OK)
        else
            ResponseEntity("""{"result":"OK", "item": ${request.id}}""", HttpStatus.OK)
    }

    @GetMapping("getSomething/{itemId}", produces = ["application/json"])
    fun getSomething(@PathVariable itemId: Int): ResponseEntity<String> {
        val ran = random.nextInt()
        return if (ran % 2 != 0) {
            val error = when {
                ran % 3 == 0 -> "item $itemId not found"
                ran % 5 == 0 -> "item $itemId: something bad happened"
                else -> "item $itemId: unhandled error"
            }
            ResponseEntity("""{"result":"KO", "item": ${itemId}, "error":"$error"}""", HttpStatus.OK)
        } else
            ResponseEntity("""{"result":"OK", "item": ${itemId}}""", HttpStatus.OK)
    }

    @PostMapping("insertSomething", consumes = ["application/json"])
    fun insertSomething(@RequestBody request: Application.Item): ResponseEntity<String> {
        val ran = random.nextInt()
        return if (ran % 2 != 0) {
            val error = when {
                ran % 3 == 0 -> "item ${request.id} not conform"
                ran % 5 == 0 -> "item ${request.id} contains wrong data"
                else -> "impossible insert item: ${request.id} "
            }
            ResponseEntity("""{"result":"KO", "item": ${request.id}, "error":"$error"}""", HttpStatus.OK)
        } else
            ResponseEntity("""{"result":"OK", "item": ${request.id}}""", HttpStatus.OK)
    }
}