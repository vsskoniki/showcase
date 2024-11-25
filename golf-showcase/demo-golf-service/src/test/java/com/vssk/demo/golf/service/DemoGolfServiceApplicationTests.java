package com.vssk.demo.golf.service;

import com.vssk.demo.golf.service.model.PlayerGameScore;
import com.vssk.demo.golf.service.model.ScoreBoard;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoGolfServiceApplicationTests {
	@Autowired
	TestRestTemplate testRestTemplate;
	@Autowired
	SimpMessagingTemplate messagingTemplate;


	private static GenericContainer<?> redis;

	@BeforeAll
	static void beforeAll() {
		redis = new GenericContainer<>(DockerImageName.parse("redis:7.0-alpine")).withExposedPorts(6379);
		redis.start();
		System.setProperty("spring.data.redis.host", redis.getHost());
		System.setProperty("spring.data.redis.port", redis.getMappedPort(6379).toString());
	}

	@AfterAll
	static void afterAll() {
		redis.stop();
	}

	@Test
	void endToEnd() throws InterruptedException {
		var score = new PlayerGameScore("test1",
				1, 2, "");
		// Set headers if needed
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		// Create an HttpEntity with the request body and headers
		HttpEntity<PlayerGameScore> entity = new HttpEntity<>(score, headers);

		// Send the POST request
		ResponseEntity response = testRestTemplate.exchange(
				"/api/score",
				HttpMethod.POST,
				entity,
				Void.class
		);

		// Assert the response status code
		assertEquals(HttpStatus.OK, response.getStatusCode());

		Thread.sleep(15000);
		var scoreBoard = testRestTemplate.getForObject("/api/score-board", ScoreBoard.class);
		assertEquals(1, scoreBoard.rows().size());

	}

}
