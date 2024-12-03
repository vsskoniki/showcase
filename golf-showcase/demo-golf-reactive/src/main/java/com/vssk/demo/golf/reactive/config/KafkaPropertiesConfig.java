package com.vssk.demo.golf.reactive.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaPropertiesConfig {
    String bootstrapServers;
    Map<String, String> consumer;
    Map<String, String> producer;

}
