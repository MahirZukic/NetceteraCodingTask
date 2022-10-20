package de.netcetera.netceteratask.service;

import de.netcetera.netceteratask.config.RedissonConfig;
import de.netcetera.netceteratask.domain.enums.GameResult;
import de.netcetera.netceteratask.domain.model.Game;
import de.netcetera.netceteratask.domain.repository.GameRepository;
import de.netcetera.netceteratask.rest.exception.GameAlreadyFinishedException;
import de.netcetera.netceteratask.rest.exception.InvalidMoveException;
import de.netcetera.netceteratask.util.GameStateChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    public static final String GAME_PREFIX = "GAME";

    private final RedissonConfig redissonConfig;
    private final GameRepository repository;

    public Game getGame(long gameId) {
        RedissonClient redissonClient = redissonConfig.redissonClient();
        RMap<Long, Game> games = redissonClient.getMap(GAME_PREFIX, new JsonJacksonCodec());
        Game game = games.get(gameId);
        if (game == null) {
            return repository.findById(gameId).orElse(null);
        } else {
            return game;
        }
    }

    public Game createGame(Long playerOneId, Long playerTwoId) {
        Game game = Game.builder()
                .playerOne(playerOneId)
                .playerTwo(playerTwoId)
                .moves(null)
                .result(GameResult.GAME_CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        RedissonClient redissonClient = redissonConfig.redissonClient();
        RMap<Long, Game> games = redissonClient.getMap(GAME_PREFIX, new JsonJacksonCodec());
        Long nextRandomLong = getNextRandomLong(playerOneId, playerTwoId);
        RLock lock = redissonClient.getLock(nextRandomLong + "");
        try {
            if (lock.tryLock(redissonConfig.lockAcquireWaitTime(), redissonConfig.lockLeaseTime(), TimeUnit.SECONDS)) {
                //Action to be performed when lock is acquired.
                game.setId(nextRandomLong);
                games.put(nextRandomLong, game);
                Thread.sleep(redissonConfig.minimumTimeToSleepForUnlocking());
                lock.unlock();
            }
        } catch (Exception e) {
            log.error("Error occurred trying to acquire redis lock..", e);
            game = null;
        } finally {
            if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("game LOCK released");
            }
        }

        return game;
    }

    public Game playNextMove(Long gameId, Integer move) throws InvalidMoveException, GameAlreadyFinishedException {
        RedissonClient redissonClient = redissonConfig.redissonClient();
        RMap<Long, Game> games = redissonClient.getMap(GAME_PREFIX, new JsonJacksonCodec());
        RLock lock = redissonClient.getLock(gameId + "");
        Game game = games.get(gameId);
        GameStateChecker.checkIfMoveIsValid(game, move);
        try {
            if (lock.tryLock(redissonConfig.lockAcquireWaitTime(), redissonConfig.lockLeaseTime(), TimeUnit.SECONDS)) {
                //Action to be performed when lock is acquired.
                if (game == null) {
                    lock.unlock();
                    return null;
                } else if (GameResult.GAME_CREATED == game.getResult() && game.getMoves() == null) {
                     {
                        // game just started and it's first move
                        game.setResult(GameResult.GAME_IN_PROGRESS);
                        game.setMoves(move);
                    }
                } else if (GameResult.GAME_IN_PROGRESS == game.getResult() && game.getMoves() != null) {
                    // game just started and it's first move
                    Integer moves = game.getMoves();
                    moves = moves * 10 + move;
                    game.setMoves(moves);
                    GameStateChecker.checkMovesAndSetGameState(game);
                } else if (GameResult.GAME_CREATED != game.getResult()
                        && GameResult.GAME_IN_PROGRESS != game.getResult()
                        && game.getMoves() != null) {
                    throw new GameAlreadyFinishedException("This game has already finished. Go play another game.");
                }
                games.put(gameId, game);
                Thread.sleep(redissonConfig.minimumTimeToSleepForUnlocking());
                lock.unlockAsync();
            }
        } catch (Exception e) {
            log.error("Error occurred trying to acquire redis lock..", e);
        } finally {
            if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("game LOCK released");
            }
        }

        return game;
    }

    private Long getNextRandomLong(Long playerOneId, Long playerTwoId) {
        Random random = new Random();
        return Math.abs(random.nextLong() + playerOneId - playerTwoId);
    }
}
