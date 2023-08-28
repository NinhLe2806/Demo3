package vn.vnpay.demo3.connection;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import vn.vnpay.demo3.config.RedisConfig;

@Slf4j
public class RedisConnection {
    private static RedisConnection instance;
    private static JedisPool jedisPool;
    private static final RedisConfig CONFIG = RedisConfig.getInstance();

    public static void initializeRedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(CONFIG.getMaxTotal());
        poolConfig.setMaxIdle(CONFIG.getMaxIdle());
        poolConfig.setMinIdle(CONFIG.getMinIdle());

        jedisPool = new JedisPool(poolConfig, CONFIG.getUrl());
        log.info("Redis pool initialized successfully.");
    }
    public static Jedis getConnection() {
        Jedis jedis = jedisPool.getResource();
        log.info("Connected to Redis server.");
        return jedis;
    }

    public static void releaseConnection(Jedis jedis) {
        try {
            if (jedis != null) {
                jedis.close();
                log.info("Connection closed successfully.");
            }
        } catch (Exception e) {
            log.error("Exception while closing connection:", e);
        }
    }
}
