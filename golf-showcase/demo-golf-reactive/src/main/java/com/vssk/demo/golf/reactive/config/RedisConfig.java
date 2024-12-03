package com.vssk.demo.golf.reactive.config;

import com.vssk.demo.golf.reactive.entity.PlayerScoresEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

    @Bean
    ReactiveRedisTemplate<String, PlayerScoresEntity> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory){
        var jdkSerializationRedisSerializer = new GenericJackson2JsonRedisSerializer();
        StringRedisSerializer stringRedisSerializer = StringRedisSerializer.UTF_8;
        GenericToStringSerializer<PlayerScoresEntity> longToStringSerializer = new GenericToStringSerializer<>(PlayerScoresEntity.class);
        ReactiveRedisTemplate<String, PlayerScoresEntity> template = new ReactiveRedisTemplate<>(connectionFactory,
                RedisSerializationContext.<String, PlayerScoresEntity>newSerializationContext(jdkSerializationRedisSerializer)
                        .key(stringRedisSerializer).value(longToStringSerializer).build());
        return template;
    }
}
