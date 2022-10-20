package de.netcetera.netceteratask.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@TestConfiguration
@Profile("test")
@Getter
@Component
public class RedisProperties {
    private int redisPort;
    private String redisHost;

    public RedisProperties(
      @Value("${spring.redis.port}") int redisPort,
      @Value("${spring.redis.host}") String redisHost) {
//      @Value("${in-memory.redis.port}") int redisPort,
//      @Value("${in-memory.redis.host}") String redisHost) {
        this.redisPort = redisPort;
        this.redisHost = redisHost;
    }

}