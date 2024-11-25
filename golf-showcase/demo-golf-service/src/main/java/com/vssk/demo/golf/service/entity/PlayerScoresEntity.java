package com.vssk.demo.golf.service.entity;

import com.vssk.demo.golf.service.model.PlayerGameScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@RedisHash("playerScores")
public class PlayerScoresEntity {
    @Id
    private String id;
    private String name;
    private List<PlayerGameScore> gameScores;
    private int total;
}
