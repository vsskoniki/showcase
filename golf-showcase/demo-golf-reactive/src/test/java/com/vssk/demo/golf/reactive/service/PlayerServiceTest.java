package com.vssk.demo.golf.reactive.service;

import com.vssk.demo.golf.reactive.TestcontainersConfiguration;
import com.vssk.demo.golf.reactive.model.PlayerGameScore;
import com.vssk.demo.golf.reactive.model.ScoreBoard;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;


@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class PlayerServiceTest {
    @MockitoBean
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    PlayerService playerService;
    @Autowired
    ReactiveRedisTemplate reactiveRedisTemplate;

    private static GenericContainer<?> redis;

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

    @BeforeEach
    void clean(){
        reactiveRedisTemplate.opsForHash().delete("playerScores");
    }

    @Test
    void testAddScoresAndScore(){
        System.out.println("ok");
        StepVerifier.create(playerService.addScore(new PlayerGameScore("test1",
                1, 2, "")))
                .expectNext(true)
                .verifyComplete();
        System.out.println("data added");
        StepVerifier.create(playerService.addScore(new PlayerGameScore("test2",
                1, 1, "")))
                .expectNext(true)
                .verifyComplete();
        System.out.println("data added");
        var mono = playerService.getScoreBoard();
        StepVerifier.create(mono)
                .consumeNextWith(sb -> {
                    System.out.println("rows"+sb.getRows());
                    assertEquals("test2", sb.getRows().getFirst().getFirst());
                })
                .verifyComplete();

        Mockito.verify(messagingTemplate, Mockito.times(2)).convertAndSend(anyString(), anyMap());
    }

    @Test
    void testAddScoreAlert(){
        StepVerifier.create(playerService.addScore(new PlayerGameScore("test3",
                1, 2, "")))
                .expectNext(true)
                .verifyComplete();
        System.out.println("data added");
        var mono = playerService.getScoreBoard();
        StepVerifier.create(mono)
                .consumeNextWith(sb -> {
                    System.out.println("rows"+sb.getRows());
                    assertEquals("test3", sb.getRows().getFirst().getFirst());
                })
                .verifyComplete();
        var expected = Map.of("message", "Player added score",
                "alert", "Player test3 scored below Par");
        Mockito.verify(messagingTemplate).convertAndSend("/topic/scores", expected);
        /*var rows = playerService.getScoreBoard().rows();
        assertEquals("test1", rows.getFirst().getFirst());
        var expected = Map.of("message", "Player added score",
                "alert", "Player test1 scored below Par");
        Mockito.verify(messagingTemplate).convertAndSend("/topic/chat", expected);

         */
    }
}
