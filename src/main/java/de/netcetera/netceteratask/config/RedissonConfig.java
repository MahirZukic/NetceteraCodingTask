package de.netcetera.netceteratask.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RedissonConfig {

    public static final long LOCK_ACQUIRE_WAIT_TIME_IN_SECONDS = 10L;
    public static final long LOCK_LEASE_TIME_IN_SECONDS = 10L;
    public static final long MIMIMUM_TIME_TO_SLEEP_UNTIL_LOCK_IS_ACQUIRED_IN_MILLIS = 100L;
    private Config config;

    public RedissonConfig() {
        config = new Config();
        config.useSingleServer()
//                .setAddress("redis://127.0.0.1:6379")
                .setAddress("redis://redis:6379")
                .setPingConnectionInterval(10)
                .setIdleConnectionTimeout(10);
    }

    public long lockAcquireWaitTime() {
        return LOCK_ACQUIRE_WAIT_TIME_IN_SECONDS;
    }

    public long lockLeaseTime() {
        return LOCK_LEASE_TIME_IN_SECONDS;
    }

    public long minimumTimeToSleepForUnlocking() {
        return MIMIMUM_TIME_TO_SLEEP_UNTIL_LOCK_IS_ACQUIRED_IN_MILLIS;
    }

    @Bean
    public RedissonClient redissonClient() {
        // Sync and Async API
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }


}
