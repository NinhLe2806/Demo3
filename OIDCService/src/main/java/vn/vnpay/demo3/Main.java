package vn.vnpay.demo3;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo3.connection.PostgreSQLConnection;
import vn.vnpay.demo3.connection.RedisConnection;

@Slf4j
public class Main {
    public static void main(String[] args) {
        try {
            init();
            NettyServer.start();
            log.info("Application has started successfully");
        }catch (Exception e){
            log.error("An error occured while starting application:",e);
        } catch (Throwable throwable) {
            log.error("An unexpected error occurred in the application:", throwable);
        }
    }

    private static void init() {
        log.info("Initialize connection starting...");
        PostgreSQLConnection.initializeConnectionPool();
        RedisConnection.initializeRedisPool();
        log.info("Initialize connection completed");
    }
}