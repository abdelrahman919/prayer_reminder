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

        void shutdown() {
            singleScheduler.shutdown();
            System.out.println("shutdown");
        }
    public ScheduledExecutorService getSingleScheduler() {
        return singleScheduler;
    }

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        Scheduler scheduler2 = new Scheduler();

        for (int i =0; i<4; i++){
            scheduler.schedule(() -> System.out.println(3), 1+i, TimeUnit.SECONDS);
            scheduler.schedule(() -> System.out.println(4), 1+i, TimeUnit.SECONDS);
        }


/*
        scheduler.shutdown(5,TimeUnit.SECONDS);
        scheduler2.shutdown(5,TimeUnit.SECONDS);
*/

        scheduler.singleScheduler.shutdown();
        scheduler2.singleScheduler.shutdown();




    }
}




/*    void shutdown(long delay, TimeUnit unit) {
        singleScheduler.shutdown();
        try {
            singleScheduler.awaitTermination(delay, unit);
            singleScheduler.shutdownNow();
            System.out.println("scheduler shutdown");

        //    System.out.println(b);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/