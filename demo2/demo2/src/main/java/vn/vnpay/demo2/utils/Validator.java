package vn.vnpay.demo2.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import vn.vnpay.demo2.common.Constant;
import vn.vnpay.demo2.common.RequestInfo;
import vn.vnpay.demo2.connection.RedisConnectionManager;
import vn.vnpay.demo2.dto.VerifyTokenRequest;
import vn.vnpay.demo2.handler.ReceiveTokenHandler;
import vn.vnpay.demo2.redis.RedisService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static vn.vnpay.demo2.common.Constant.CLIENT_ID;
import static vn.vnpay.demo2.common.Constant.CLIENT_SECRET;

@Slf4j
public class Validator {
    public static <T extends RequestInfo> boolean checkByTimeLimit(T requestInfo) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime requestTime = LocalDateTime.parse(requestInfo.getRequestTime(), formatter);

        // Check time between requestTime and currentTime
        long minutesBetween = ChronoUnit.MINUTES.between(requestTime, currentTime);

        if (minutesBetween > 10 || minutesBetween < -10) {
            log.info("Time limit (10 min) exceeded for request: {}", requestInfo.getRequestId());
            return false; // Request is outside the time limit
        } else {
            log.info("Request is within time limit: {}", requestInfo.getRequestId());
            return true; // Request is within the time limit
        }
    }

    public static boolean checkDuplicateRequest(String requestId) {
        RedisService redisService = new RedisService();
        Jedis jedis = null;
        boolean isDuplicate = false;

        try {
            jedis = RedisConnectionManager.getConnection();
            isDuplicate = jedis.sismember(Constant.REQUEST_ID_PREFIX, requestId);

            if (isDuplicate) {
                log.info("RequestID is duplicate within a day: {}", requestId);
            } else {
                log.info("RequestID is valid: {}", requestId);
                redisService.saveRequestIdInRedis(requestId);
            }
        } catch (JedisException e) {
            // Xử lý ngoại lệ liên quan đến kết nối Redis Exception handle relate to Redis connection
            log.error("Error while working with Redis: {}", e.getMessage());
        } finally {
            RedisConnectionManager.releaseConnection(jedis);
        }
        return !isDuplicate;
    }

    public static boolean isRequestNull(String requestBody) {
        if (StringUtils.isBlank(requestBody) || requestBody.trim().replaceAll("\\s", "").equals("{}")) {
            return true;
        }
        return false;
    }

    public static boolean isValidUser(ChannelHandlerContext ctx, FullHttpRequest request) {
        log.info("Begin isValidUser...");
        if (null != request) {
            String accessToken = ReceiveTokenHandler.getAccessToken(request);
            if (!StringUtils.isBlank(accessToken)) {
                VerifyTokenRequest verifyTokenRequest = VerifyTokenRequest
                        .builder()
                        .accessToken(accessToken)
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .build();
                ExternalApiSender.getInstance().verifyAccessToken(ctx, verifyTokenRequest);
                return true;
            }
            log.info("AccessToken is invalid");
            return false;
        }
        log.info("Request is null in isValidUser");
        return false;
    }
}
