package com.vssk.demo.golf.service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vssk.demo.golf.service.model.PlayerGameScore;
import com.vssk.demo.golf.service.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PriceEventListener {
    private final PlayerService playerService;
    private final ObjectMapper objectMapper;

    public PriceEventListener(PlayerService playerService, ObjectMapper objectMapper) {
        this.playerService = playerService;
        this.objectMapper = objectMapper;
    }

    /**
     * Kafka endpoint for consuming a batch of scores, concurrency is configured in yaml
     * Process a list of score events group by player and scale out processing by player
     */
    @KafkaListener(id = "listenScores", topics = "${player.score-topic}", containerFactory = "batchFactory")
    public void listenScores(List<ConsumerRecord<String,String>> scores) {
        log.info("Received events {}", scores);
        scores.stream()
                .collect(Collectors.groupingBy(ConsumerRecord::key))
                .entrySet()
                .parallelStream()
                .forEach(stringListEntry -> {
                    stringListEntry.getValue().forEach(score -> {
                        try {
                            playerService.addScore(objectMapper.readValue(score.value(), PlayerGameScore.class));
                        } catch (JsonProcessingException e) {
                            log.error("Failed to deserialize {}", score, e);
                            //TODO: store failes for review
                        }
                    });
                });
    }
}