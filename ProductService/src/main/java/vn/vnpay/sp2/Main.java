package vn.vnpay.sp2;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.sp2.config.NettyServer;
import vn.vnpay.sp2.connection.PostgresConnection;

@Slf4j
public class Main {
    public static void main(String[] args) {
        try {
            init();
            NettyServer.start();
            log.info("Application has started successfully");
        }catch (Exception e){
            log.info("Exception in Main:{}", e.getMessage());
            log.error("An error occured while starting application:",e);
        } catch (Throwable throwable) {
            log.info("An unexpected error occurred in the application:{}", throwable.getMessage());
            log.error("An unexpected error occurred in the application:", throwable);
        }
    }

    private static void init() {
        log.info("Initialize connection starting...");
        PostgresConnection.initializeConnectionPool();
        log.info("Initialize connection completed");
    }
}