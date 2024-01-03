package com.hamada;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    private final ScheduledExecutorService singleScheduler = Executors.newSingleThreadScheduledExecutor();

    ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        try {
            return singleScheduler.schedule(command, delay, unit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    void shutdown(long delay, TimeUnit unit) {
        try {
            singleScheduler.shutdown();
            boolean b = singleScheduler.awaitTermination(delay, unit);
            System.out.println("scheduler shutdown");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
