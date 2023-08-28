package vn.vnpay.demo2.connection;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class RedisConnectionManager {
    private static JedisPool jedisPool;

    public static void initializeRedisPool() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = RedisConnectionManager.class
                    .getClassLoader()
                    .getResourceAsStream("config.properties");
            properties.load(inputStream);

            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(Integer.parseInt(properties.getProperty("redis.maxTotal")));
            poolConfig.setMaxIdle(Integer.parseInt(properties.getProperty("redis.maxIdle")));
            poolConfig.setMinIdle(Integer.parseInt(properties.getProperty("redis.minIdle")));

            jedisPool = new JedisPool(poolConfig, properties.getProperty("redis.url"));
            log.info("Redis pool initialized successfully.");
        } catch (IOException e) {
            log.info("Error while reading config.properties: {}", e.getMessage(), e);
        }
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
