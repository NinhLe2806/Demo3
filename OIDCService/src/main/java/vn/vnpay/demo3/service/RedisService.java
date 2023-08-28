package vn.vnpay.demo3.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import vn.vnpay.demo3.common.Constant;
import vn.vnpay.demo3.connection.RedisConnection;

import java.util.Objects;

import static vn.vnpay.demo3.common.Constant.LOGGED_IN_USER;

@Slf4j
public class RedisService {
    private static RedisService instance;

    public static RedisService getInstance() {
        if (Objects.isNull(instance)) {
            instance = new RedisService();
        }
        return instance;
    }

    public void saveRefreshToken(String clientId, String userId, String refreshToken, Long expirationTime) {
        log.info("Begin saveRefreshToken for clientId:{} and userId:{}", clientId, userId);
        Jedis jedis = null;

        try {
            jedis = RedisConnection.getConnection();

            String key = createKey(Constant.REFRESH_TOKEN_PREFIX, clientId);
            jedis.hset(key, userId, refreshToken);
            jedis.expire(key, expirationTime.intValue()/1000);
            log.info("Set refreshToken successful for userId:{}", userId);
        } catch (Exception e) {
            log.error("An error occurred while saving the refresh token in Redis.", e);
        } finally {
            RedisConnection.releaseConnection(jedis);
        }
    }

    public String getRefreshToken(String clientId, String userId) {
        log.info("Begin getRefreshToken for clientId:{} and userId:{}", clientId, userId);
        Jedis jedis = null;

        try {
            jedis = RedisConnection.getConnection();

            String key = createKey(Constant.REFRESH_TOKEN_PREFIX, clientId);
            return jedis.hget(key, userId);
        } catch (Exception e) {
            log.error("An Exception occurred while getting the refresh token in Redis:", e);
            return null;
        } finally {
            RedisConnection.releaseConnection(jedis);
        }
    }

    private String createKey(String prefix, String suffix) {
        return new StringBuilder().append(prefix).append(suffix).toString();
    }

    public void saveLoggedInUser(String userId, String accessToken, Long expirationTime){
        log.info("Begin saveLoggedInUser for userId:{}", userId);
        Jedis jedis = null;

        try {
            jedis = RedisConnection.getConnection();

            String key = createKey(LOGGED_IN_USER, userId);
            jedis.set(key, accessToken);
            jedis.pexpire(key, expirationTime);
            log.info("Set refreshToken successful for userId:{}", userId);
        } catch (Exception e) {
            log.error("An Exception in saveLoggedInUser:", e);
        } finally {
            RedisConnection.releaseConnection(jedis);
        }
    }

    public String getAccessKeyFromLoggedInUser(String userId){
        log.info("Begin getAccessKeyFromLoggedInUser for userId:{}", userId);
        Jedis jedis = null;

        try {
            jedis = RedisConnection.getConnection();

            String key = createKey(LOGGED_IN_USER, userId);
            String accessKey = jedis.get(key);
            if(StringUtils.isBlank(accessKey)){
                log.info("Not found any key with userid {} in getAccessKeyFromLoggedInUser", userId);
                return null;
            }
            log.info("Get accessKey successful for userId:{} in getAccessKeyFromLoggedInUser", userId);
            return accessKey;
        } catch (Exception e) {
            log.info("An Exception in saveLoggedInUser:{}", e.getMessage());
            log.error("An Exception in saveLoggedInUser:", e);
            return null;
        } finally {
            RedisConnection.releaseConnection(jedis);
        }
    }
}
