package com.vssk.demo.golf.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vssk.demo.golf.service.model.PlayerGameScore;
import com.vssk.demo.golf.service.model.ScoreBoard;
import com.vssk.demo.golf.service.service.PlayerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class PlayerController {
    private final PlayerService playerService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String scoreTopic;
    private final ObjectMapper objectMapper;

    public PlayerController(PlayerService playerService,
                            KafkaTemplate<String, String> kafkaTemplate,
                            @Value("${player.score-topic}")
                            String scoreTopic, ObjectMapper objectMapper) {
        this.playerService = playerService;
        this.kafkaTemplate = kafkaTemplate;
        this.scoreTopic = scoreTopic;
        this.objectMapper = objectMapper;
    }

    /**
     * REST End point where players will add there score
     *
     */
    @PostMapping("/score")
    @ResponseStatus(HttpStatus.OK)
    public void addScore(@RequestBody PlayerGameScore playerGameScore) throws JsonProcessingException {
        kafkaTemplate.send(scoreTopic, playerGameScore.getName(), objectMapper.writeValueAsString(playerGameScore));
        //playerService.addScore(playerGameScore);
    }

    /**
     * REST Endpoint for return a score board to display on UI
     * @return ScoreBoard
     */
    @GetMapping("/score-board")
    public ScoreBoard getScoreBoard(){
        return playerService.getScoreBoard();
    }
}
