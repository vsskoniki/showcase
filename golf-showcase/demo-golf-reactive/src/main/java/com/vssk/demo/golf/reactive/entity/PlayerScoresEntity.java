package com.vssk.demo.golf.reactive.entity;


import com.vssk.demo.golf.reactive.model.PlayerGameScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("playerScores")
public class PlayerScoresEntity {
    @Id
    private String id;
    private String name;
    private List<PlayerGameScore> gameScores;
    private int total;
}
