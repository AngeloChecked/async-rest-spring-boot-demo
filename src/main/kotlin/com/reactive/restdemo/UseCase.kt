package com.reactive.restdemo

import org.slf4j.LoggerFactory

class UseCase(private val restGateway: AsyncRestGateway) {
    fun execute(item: Application.Item) {
        val getFuture = restGateway.get(item.id)
        val insertFuture = restGateway.insert(item)
        val finalFuture = restGateway.final(item)
        getFuture.subscribe { get ->
                logger.info("for item $item get service response: $get")

                insertFuture.subscribe { insert ->
                    logger.info("for item $item insert service response: $insert")

                    finalFuture.subscribe { final ->
                        logger.info("""
                            |----- final result ----
                            |for item $item get service response: $get
                            |for item $item insert service response: $insert
                            |for item $item final service response: $final
                            |
                        """.trimMargin())

                    }
                }
            }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UseCase::class.java)
    }
}