package com.vssk.demo.golf.reactive.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vssk.demo.golf.reactive.model.PlayerGameScore;
import com.vssk.demo.golf.reactive.service.PlayerService;
import com.vssk.demo.golf.reactive.util.Tournament;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.servlet.function.RequestPredicates.accept;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration
public class RoutesConfig {


    @Bean
    RouterFunction<ServerResponse> routes(PlayerService playerService){
        return route()
                .GET("/api/tournament",accept(APPLICATION_JSON),(r ->
                        {
                            System.out.println("request for tournament"+r.headers());
                            var origin = r.headers().header("Origin").getFirst();
                return ServerResponse.ok()
                        .header("Access-Control-Allow-Origin",origin)
                        .body(
                        Tournament.getInstance());
                        })
                )
                .GET("/api/score-board",accept(APPLICATION_JSON),(r ->
                        getScoreBoard(r, playerService)))
                .build();
    }

    private static ServerResponse getScoreBoard(ServerRequest r, PlayerService playerService) {
        var origin = r.headers().header("Origin").getFirst();
        return ServerResponse.ok()
                .header("Access-Control-Allow-Origin", origin)
                .body(
                        playerService.getScoreBoard()
                );
    }
}
