package vn.vnpay.demo2.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CronJobScheduler {
    private ScheduledExecutorService scheduler;

    public CronJobScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void startCronJob(Runnable task, long initialDelay, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    public void stopCronJob() {
        scheduler.shutdown();
    }
}
