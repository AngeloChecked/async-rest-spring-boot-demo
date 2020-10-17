package com.reactive.restdemo

import com.reactive.restdemo.Application.Item
import com.reactive.restdemo.config.TestConfiguration
import com.reactive.restdemo.config.TestGateway
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT

@SpringBootTest(webEnvironment = DEFINED_PORT, classes = [Application::class, TestConfiguration::class])
class ApplicationTests {

	@Autowired
	lateinit var testGateway: TestGateway

	@Test
	fun contextLoads() {
		repeat(525) {
			testGateway.sendInit(Item(it))
		}



		Thread.sleep(10000)
	}

}
