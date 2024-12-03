package com.vssk.demo.golf.reactive.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vssk.demo.golf.reactive.model.PlayerGameScore;
import com.vssk.demo.golf.reactive.service.PlayerService;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

import java.util.List;
import java.util.Properties;

@Configuration
@EnableKafka
@EnableConfigurationProperties
@AutoConfigureAfter(KafkaAutoConfiguration.class)
public class KafkaConfig {
    @Bean
    public ReceiverOptions<String, String> kafkaReceiverOptions(KafkaPropertiesConfig kafkaProperties,
                                                                @Value("${score.topic}")
                                                                String topic){
        System.out.println("kafka="+kafkaProperties.getConsumer());
        var properties = new Properties();
        properties.putAll(kafkaProperties.getConsumer());
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers);
        System.out.println(""+properties);
        ReceiverOptions<String, String> options = ReceiverOptions.create(properties);
        return options.subscription(List.of(topic));
    }

    @Bean
    ReactiveKafkaConsumerTemplate<String, String> kafkaConsumerTemplate(ReceiverOptions<String,String> receiverOptions){
        return new ReactiveKafkaConsumerTemplate<String, String>(receiverOptions);
    }
    @Bean
    ReactiveKafkaProducerTemplate<String, String> kafkaProducerTemplate(KafkaPropertiesConfig kafkaProperties){
        var properties = new Properties();
        properties.putAll(kafkaProperties.getProducer());
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers);
        return new ReactiveKafkaProducerTemplate<String, String>(SenderOptions.create(properties));
    }

    @Bean
    public ApplicationRunner kafkaConsumer(ReactiveKafkaConsumerTemplate<String, String> consumerTemplate,
                                           PlayerService playerService,
                                           ObjectMapper objectMapper) {
        return args -> {
            System.out.println("Application started!");
            consumerTemplate
                    .receiveBatch()
                    .concatMap(b -> b)
                    .groupBy(v -> v.key())
                    .flatMap(group -> group.publishOn(Schedulers.parallel()))
                    .flatMap( stringStringReceiverRecord -> {

                        System.out.println("received kafka message "+stringStringReceiverRecord.value());
                        try {
                            var score = objectMapper.readValue(stringStringReceiverRecord.value(), PlayerGameScore.class);
                            return playerService.addScore(score);
                        } catch (JsonProcessingException e) {
                            System.out.println("handle error "+e);
                        }
                        return Mono.empty();
                    })
                    .map(r -> r)
                    .subscribe();
        };
    }
}
