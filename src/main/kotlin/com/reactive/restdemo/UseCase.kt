package com.reactive.restdemo

import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux

class UseCase(private val restGateway: AsyncRestGateway) {
    fun execute(item: Application.Item) {

        val orchestration = Flux.create<List<AsyncRestGateway.Result>> { orch ->
            restGateway.get(item.id).subscribe { get ->
                if (get is AsyncRestGateway.Result.Failure) {
                    restGateway.insert(item).subscribe { insert ->
                        if (insert is AsyncRestGateway.Result.Success) {
                            restGateway.final(item).subscribe { final ->
                                orch.next(listOf(get, insert, final)).complete()
                            }
                        } else orch.next(listOf(get, insert)).complete()
                    }
                } else
                    restGateway.final(item).subscribe { final ->
                        orch.next(listOf(get, final)).complete()
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