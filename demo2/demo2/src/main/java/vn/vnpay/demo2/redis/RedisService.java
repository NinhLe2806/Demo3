package vn.vnpay.demo2.redis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import vn.vnpay.demo2.connection.RedisConnectionManager;
import vn.vnpay.demo2.common.Constant;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
public class RedisService {
    public void saveRequestIdInRedis(String requestId) {
        Jedis jedis = null;

        try {
            jedis = RedisConnectionManager.getConnection();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endTime = now.withHour(23).withMinute(59).withSecond(59);
            long expireTimestamp = endTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();

            long addedCount = jedis.sadd(Constant.REQUEST_ID_PREFIX, requestId);
            if (addedCount > 0) {
                log.info("RequestId {} added to Redis Set successfully", requestId);
            } else {
                log.info("RequestId {} already exists in Redis Set, not added", requestId);
            }
            jedis.expireAt(Constant.REQUEST_ID_PREFIX, expireTimestamp);

        } catch (Exception e) {
            log.info("An error occurred while saving the request ID in Redis.", e);
        } finally {
            RedisConnectionManager.releaseConnection(jedis);
        }
    }
}
