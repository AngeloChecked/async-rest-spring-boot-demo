package com.reactive.restdemo

import com.reactive.restdemo.Application.*
import com.reactive.restdemo.AsyncGenericRestGateway.ApplicativeFailureException.*
import com.reactive.restdemo.AsyncRestGateway.Result
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

class UseCase(private val restGateway: AsyncRestGateway) {
    fun execute(item: Item) {
        val orchestration = Flux.create<Result> { orch ->
            restGateway.get(item.id)
                .doOnError(GetException::class.java, invokeInsertAndFinalRequest(orch, item))
                .subscribe(invokeFinalRequest(orch))
        }

          orchestration
              .collectList()
              .subscribe { results ->
              val logResults = results.mapIndexed { i, result ->
                  "result $i: $result"
              }.joinToString("\n")
              logger.info("""
                              |----- final result ----
                              |$logResults
                              |
                          """.trimMargin())
          }
    }

    private fun invokeInsertAndFinalRequest(orch: FluxSink<Result>, item: Item): (GetException) -> Unit = { getErr ->
        orch.next(getErr.error)
        restGateway.insert(item)
            .doOnError(InsertException::class.java) {
                orch.next(it.error).complete()
            }
            .subscribe(invokeFinalRequest(orch))
    }

    private fun invokeFinalRequest(orch: FluxSink<Result>): (Result) -> Unit {
        return { result ->
            orch.next(result)
            restGateway.final(Item((result as Result.Success).item))
                .doOnError(FinalException::class.java) { orch.next(it.error).complete() }
                .subscribe { orch.next(it).complete() }
        }
    }


    companion object {
        private val logger = LoggerFactory.getLogger(UseCase::class.java)
    }
}