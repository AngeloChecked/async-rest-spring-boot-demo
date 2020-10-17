package com.reactive.restdemo

import org.slf4j.LoggerFactory

class UseCase(private val restGateway: RestGateway) {
    fun execute(item: Application.Item) {
		val get = restGateway.get(item.id)
		logger.info("\n\n>>>get>>>\nfor item $item get service response: \n$get\n")
		val insert = restGateway.insert(item)
		logger.info("\n\n>>>inset>>>\nfor item $item insert service response: \n$insert\n")
		val final = restGateway.final(item)
		logger.info("\n\n>>>final>>>\nfor item $item final service response: \n$final\n")
	}

	companion object {
		private val logger = LoggerFactory.getLogger(UseCase::class.java)
	}
}