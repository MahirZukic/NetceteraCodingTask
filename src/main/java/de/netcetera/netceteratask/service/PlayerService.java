package de.netcetera.netceteratask.service;

import de.netcetera.netceteratask.config.RedissonConfig;
import de.netcetera.netceteratask.domain.model.Player;
import de.netcetera.netceteratask.domain.repository.PlayerRepository;
import de.netcetera.netceteratask.rest.dto.PlayerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    public static final String PLAYER_PREFIX = "PLAYER";

    private final RedissonConfig redissonConfig;
    private final PlayerRepository repository;

    public Player createPlayer(PlayerDto dto) {
        Player player = null;
        RedissonClient redissonClient = redissonConfig.redissonClient();
        RMap<Long, Player> players = redissonClient.getMap(PLAYER_PREFIX, new JsonJacksonCodec());
        Long nextRandomLong = getNextRandomLong();
        RLock lock = redissonClient.getLock(nextRandomLong + "");
        try {
            if (lock.tryLock(redissonConfig.lockAcquireWaitTime(), redissonConfig.lockLeaseTime(), TimeUnit.SECONDS)) {
                //Action to be performed when lock is acquired.
                player = player.builder()
                        .id(nextRandomLong)
                        .username(dto.getUsername())
                        .email(dto.getEmail())
                        .build();
                players.put(nextRandomLong, player);
                repository.save(player);
                Thread.sleep(redissonConfig.minimumTimeToSleepForUnlocking());
//                lock.unlock();
            }
        } catch (Exception e) {
            log.error("Error occurred trying to acquire redis lock..", e);
            player = null;
        } finally {
            if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("player LOCK released");
            }
        }

        return player;
    }

    public Player getPlayer(Long playerId) {
        RedissonClient redissonClient = redissonConfig.redissonClient();
        RMap<Long, Player> players = redissonClient.getMap(PLAYER_PREFIX);
        return players.get(playerId);
    }

    private Long getNextRandomLong() {
        Random random = new Random();
        return Math.abs(random.nextLong());
    }

}
