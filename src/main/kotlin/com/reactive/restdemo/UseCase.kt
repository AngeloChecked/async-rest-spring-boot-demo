package com.reactive.restdemo

import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class UseCase(private val restGateway: AsyncRestGateway) {
    fun execute(item: Application.Item) {
        val getFuture = restGateway.get(item.id)
        val insertFuture = restGateway.insert(item)
        val finalFuture = restGateway.final(item)

        val orchestration = Flux.create<List<AsyncRestGateway.Result>> { orch ->
            getFuture.subscribe { get ->
                orch.next(listOf(get))
                insertFuture.subscribe { insert ->
                    orch.next(listOf(get, insert))
                    finalFuture.subscribe { final ->
                        orch.next(listOf(get, insert, final)).complete()
                    }
                }
            }
        }

        orchestration.subscribe { results ->
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


    companion object {
        private val logger = LoggerFactory.getLogger(UseCase::class.java)
    }
}