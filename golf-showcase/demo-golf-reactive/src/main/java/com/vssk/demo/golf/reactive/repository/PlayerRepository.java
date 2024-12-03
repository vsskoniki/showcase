package com.vssk.demo.golf.reactive.repository;


import com.vssk.demo.golf.reactive.entity.PlayerScoresEntity;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class PlayerRepository {
    private final ReactiveRedisTemplate<String, PlayerScoresEntity> reactiveRedisTemplate;


    public PlayerRepository(ReactiveRedisTemplate<String,PlayerScoresEntity> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Boolean> save(PlayerScoresEntity entity){
        return reactiveRedisTemplate.opsForHash().put("playerScores", entity.getId(), entity);
    }

    public Mono<PlayerScoresEntity> findById(String id){
        return reactiveRedisTemplate.<String,PlayerScoresEntity>opsForHash().get("playerScores", id);
    }
}
