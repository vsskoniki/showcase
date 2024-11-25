package com.vssk.demo.golf.service.service;

import com.vssk.demo.golf.service.model.PlayerGameScore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
public class PlayerServiceTest {
    @MockitoBean
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    PlayerService playerService;

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
    void testAddScoresAndScore(){
        System.out.println("ok");
        playerService.addScore(new PlayerGameScore("test1",
                1, 2, ""));

        playerService.addScore(new PlayerGameScore("test2",
                1, 1, ""));
        var rows = playerService.getScoreBoard().rows();
        assertEquals("test2", rows.getFirst().getFirst());
        Mockito.verify(messagingTemplate, Mockito.times(2)).convertAndSend(anyString(), anyMap());
    }

    @Test
    void testAddScoreAlert(){
        playerService.addScore(new PlayerGameScore("test1",
                1, 2, ""));
        var rows = playerService.getScoreBoard().rows();
        assertEquals("test1", rows.getFirst().getFirst());
        var expected = Map.of("message", "Player added score",
                "alert", "Player test1 scored below Par");
        Mockito.verify(messagingTemplate).convertAndSend("/topic/scores", expected);
    }
}
