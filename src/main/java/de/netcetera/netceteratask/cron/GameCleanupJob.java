package de.netcetera.netceteratask.cron;

import de.netcetera.netceteratask.config.RedissonConfig;
import de.netcetera.netceteratask.domain.enums.GameResult;
import de.netcetera.netceteratask.domain.model.Game;
import de.netcetera.netceteratask.domain.repository.GameRepository;
import de.netcetera.netceteratask.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameCleanupJob {

    private final RedissonConfig redissonConfig;
    private final GameRepository repository;

    @Value("${GameCleanupJob.ageInMinutesToCleanup}")
    private Integer ageInMinutesToCleanup;

    public static final int MILLISECONDS_IN_SECOND = 1000;

//    GameCleanupJob.repeatingEverySeconds
//    @Scheduled(fixedRate = MILLISECONDS_IN_SECOND * 5L, initialDelay = 0L)
//    @Scheduled(cron = "0/5 * * * * ?")
    @Scheduled(cron = "0/90 * * * * ?")
    public void cleanUpGames() {
        log.info("Starting a scheduled clean up job to ease the load on Redis server");
        RedissonClient redissonClient = redissonConfig.redissonClient();
        RMap<Long, Game> games = redissonClient.getMap(GameService.GAME_PREFIX, new JsonJacksonCodec());
        List<Game> gamesToSave = new ArrayList<>();
        games.entrySet()
                .parallelStream()
                .forEach(gameEntry -> {
                    Game game = gameEntry.getValue();
                    if (shouldBeCleanedUp(game)) {
                        gamesToSave.add(game);
                    }
                });
        try {
            if (!gamesToSave.isEmpty()) {
                Long[] gamesToClean = new Long[gamesToSave.size()];
                for (int i = 0; i < gamesToSave.size(); i++) {
                    gamesToClean[i] = gamesToSave.get(i).getId();
                }
                repository.saveAllAndFlush(gamesToSave);
                games.fastRemoveAsync(gamesToClean);
            }
        } catch (Exception exception) {
            log.error("An error occurred.", exception);
        }
    }

    private boolean shouldBeCleanedUp(Game game) {
        return GameResult.isFinished(game) || isOldEnough(game);
    }

    private boolean isOldEnough(Game game) {
        long minutes = ChronoUnit.MINUTES.between(game.getCreatedAt(), LocalDateTime.now());
//        return minutes >= 5L;
        return minutes >= ageInMinutesToCleanup;
    }


}
