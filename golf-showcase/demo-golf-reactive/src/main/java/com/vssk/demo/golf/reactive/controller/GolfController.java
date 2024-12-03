package com.vssk.demo.golf.reactive.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vssk.demo.golf.reactive.model.PlayerGameScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class GolfController {

    @Autowired
    ReactiveKafkaProducerTemplate<String, String> kafkaProducerTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Value("${score.topic}")
    String topic;

    @PostMapping(value = "/score", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> addScore(@RequestBody PlayerGameScore score) throws JsonProcessingException {
        System.out.println("adding score");
        try {
            return kafkaProducerTemplate.send(topic,score.getName(), objectMapper.writeValueAsString(score))
                    .flatMap(result -> Mono.empty());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
