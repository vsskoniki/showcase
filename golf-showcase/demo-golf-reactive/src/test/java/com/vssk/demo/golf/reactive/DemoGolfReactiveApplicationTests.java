package com.vssk.demo.golf.reactive;

import com.vssk.demo.golf.reactive.model.PlayerGameScore;
import com.vssk.demo.golf.reactive.model.ScoreBoard;
import com.vssk.demo.golf.reactive.util.Tournament;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoGolfReactiveApplicationTests {

	private static GenericContainer<?> redis;

	@Autowired
	WebTestClient webTestClient;

	//@BeforeAll
	static void beforeAll() {
		redis = new GenericContainer<>(DockerImageName.parse("redis:7.0-alpine")).withExposedPorts(6379);
		redis.start();
		System.setProperty("spring.data.redis.host", redis.getHost());
		System.setProperty("spring.data.redis.port", redis.getMappedPort(6379).toString());
	}

	//@AfterAll
	static void afterAll() {
		redis.stop();
	}

	@Test
	void testGetTournament() {
		webTestClient
				.get()
				.uri("/api/tournament")
				.header("Origin", "http://localhost:3000")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().valueEquals("Access-Control-Allow-Origin", "http://localhost:3000")
				.expectBody(Tournament.class)
		;
	}

	@Test
	void endToEnd() throws InterruptedException {
		var score = new PlayerGameScore("test1",
				1, 2, "");

		webTestClient
				.post()
				.uri("/api/score")
				.bodyValue(score)
				.exchange()
				.expectStatus().isOk();
		Thread.sleep(15000);
		var scoreBoard = webTestClient
				.get()
				.uri("/api/score-board")
				.exchange()
				.expectStatus().isOk().expectBody(ScoreBoard.class)
				.returnResult().getResponseBody();
		assertEquals(1, scoreBoard.getRows().size());

	}

}
