package vn.vnpay.demo2;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo2.connection.NettyServer;
import vn.vnpay.demo2.connection.PostgreSQLConnection;
import vn.vnpay.demo2.connection.RedisConnectionManager;

@Slf4j
public class Demo2Application {
    public static void main(String[] args) {
        try {
            init();

            NettyServer server = new NettyServer();
            server.start();

        } catch (Exception e) {
            log.error("An error occured while starting application:", e);
        } catch (Throwable throwable) {
            log.error("An unexpected error occurred in the application:", throwable);
        }
    }

    private static void init() {
        log.info("Initialize connection starting...");
        PostgreSQLConnection.initializeConnectionPool();
        RedisConnectionManager.initializeRedisPool();
        log.info("Initialize connection completed");

//      Wiring+DI
//        Injector injector = Guice.createInjector(new AppModule());
//        FeeTransactionService feeTransactionService = injector.getInstance(FeeTransactionService.class);
//        feeTransactionService.startCronJobToUpdateTransactions(); //Start Cronjob
    }
}
