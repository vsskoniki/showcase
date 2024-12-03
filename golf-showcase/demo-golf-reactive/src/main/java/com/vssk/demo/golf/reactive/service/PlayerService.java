package com.vssk.demo.golf.reactive.service;

import com.vssk.demo.golf.reactive.entity.PlayerScoresEntity;
import com.vssk.demo.golf.reactive.model.PlayerGameScore;
import com.vssk.demo.golf.reactive.model.ScoreBoard;
import com.vssk.demo.golf.reactive.repository.PlayerRepository;
import com.vssk.demo.golf.reactive.util.Tournament;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    private final static String NS = "tournament";
    private final static String WS_CHANNEL = "/topic/scores";

    private final SimpMessagingTemplate messagingTemplate;
    private final PlayerRepository playerRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public PlayerService(SimpMessagingTemplate messagingTemplate,
                         PlayerRepository playerRepository,
                         RedisTemplate<String, String> redisTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.playerRepository = playerRepository;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Add a score , evaluate the relative par and update player level total score
     * Store the aggregated score for player with key as player name, values are list of scores
     * Store the total score in a sorted collection for quick retrieval for all players
     */
    public Mono<Boolean> addScore(PlayerGameScore score) {
        var oPlayer = playerRepository.findById(score.getName());
        var scorePar = getScorePar(score.getHole(), score.getScore());
        score.setScorePar(getScorePar(scorePar));
        Map<String, String> notify = new HashMap<>();
        notify.put("message", "Player added score");
        if (scorePar < 0) {
            notify.put("alert", "Player %s scored below Par".formatted(score.getName()));
        }
        int total = score.getScore();
        AtomicInteger finalTotal1 = new AtomicInteger(total);
        return oPlayer.flatMap(persisted -> {

            var currScores = persisted.getGameScores();
            var oHoleScore = currScores.stream().filter(s -> s.getHole() == score.getHole()).findFirst();
            if (oHoleScore.isEmpty()) {
                currScores.add(score);
            } else {
                var holScore = oHoleScore.get();
                holScore.setScore(score.getScore());
                holScore.setScorePar(getScorePar(scorePar));
            }
            Collections.sort(currScores, Comparator.comparingInt(PlayerGameScore::getHole));
            finalTotal1.set(currScores.stream().mapToInt(p -> p.getScore() != null ? p.getScore() : 0).sum());
            persisted.setTotal(finalTotal1.get());
            return playerRepository.save(persisted);

        }).switchIfEmpty( Mono.defer(() -> {
            var gameScores = new ArrayList<PlayerGameScore>();
            int[] holes = Tournament.getInstance().getHoles();
            for (int h : holes) {
                if (h != score.getHole()) {
                    gameScores.add(PlayerGameScore.builder()
                            .hole(h)
                            .name(score.getName())
                            .scorePar("")
                            .build());
                } else {
                    gameScores.add(score);
                }
            }
            var entity = PlayerScoresEntity.builder()
                    .id(score.getName())
                    .name(score.getName())
                    .gameScores(gameScores)
                    .total(finalTotal1.get())
                    .build();
            return playerRepository.save(entity);

                })
        ).doOnNext(data ->{
            redisTemplate.opsForZSet().add(NS, score.getName(), total);
            System.out.println("sending server event");
            messagingTemplate.convertAndSend(WS_CHANNEL, notify);
        });
    }

    private int getScorePar(int hole, int score) {
        var parMap = Tournament.getInstance().getHoleParMap();
        var par = parMap.get(hole);
        return score - par;
    }

    private String getScorePar(int relativePar) {
        if (relativePar < 0) {
            return Integer.toString(relativePar);
        } else if (relativePar > 0) {
            return "+" + relativePar;
        } else {
            return "E"; // Even
        }
    }

    public Mono<ScoreBoard> getScoreBoard() {
        var tournament = Tournament.getInstance();
        var labels = new ArrayList<String>();
        labels.add("Name");
        for (int h : tournament.getHoles()) {
            labels.add("Hole" + h + "-Par");
        }
        labels.add("TotalScore");
        Set<String> sortedPlayers = redisTemplate.opsForZSet().range(NS, 0, -1);
        List<List<String>> rows = new ArrayList<>();
        return Mono.just(new ScoreBoard())
                .map(sb -> {
                    sb.setFields(labels);
                    return sb;
                })
                .flatMap(sb -> Flux.fromIterable(sortedPlayers)
                        .flatMap(playerRepository::findById)
                        .map(d -> getScores(d, tournament))
                        .collect(Collectors.toList())
                        .map(
                                data -> {
                                    sb.setRows(data);
                                    return sb;
                                }
                        ));
    }

    private static List<String> getScores(PlayerScoresEntity score, Tournament tournament) {

        List<String> data = new ArrayList<>();
        var name = score.getName();
        data.add(name);
        for (PlayerGameScore gameScore : score.getGameScores()) {
            data.add("" + gameScore.getScorePar());
        }
        if (score.getGameScores().size() < tournament.getHoles().length) {
            for (int m = 0; m < (tournament.getHoles().length - score.getGameScores().size()); m++) {
                data.add("");
            }
        }
        data.add("" + score.getTotal());
        return data;
    }
}
