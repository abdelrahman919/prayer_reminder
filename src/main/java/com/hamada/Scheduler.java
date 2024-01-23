package com.hamada;

import com.hamada.gui.HomeForm;

import javax.swing.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    private final ScheduledExecutorService singleScheduler = Executors.newSingleThreadScheduledExecutor();
    public static final List<String> sortedPrayers = new ArrayList<>(List.of("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"));
    public static LocalTime fakeUpdateTime = LocalTime.of(15,16,59,999999999);

    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        try {
            return singleScheduler.schedule(command, delay, unit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void shutdown() {
        singleScheduler.shutdown();
        System.out.println("shutdown");
    }

    public ScheduledExecutorService getSingleScheduler() {
        return singleScheduler;
    }

    public void adanSchedule(Map<String, String> prayerTimings, long delay, int period) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

/*                String message = String.format("%d Minutes left until %s ",remainingTime , prayer);
                //schedule an alarm with different delay for each prayer
                scheduler.schedule(
                        MyTrayIcon.displayTrayRunnable(message, trayIcon),
                        delay , unit);*/

    }

    public static void updateTimings(Map<String, String> timings, HomeForm homeForm) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
/*        int timeUntilNextDay = (int) Duration.between(LocalTime.now(),
                LocalTime.MAX).toMillis()+500;*/
        int timeUntilNextDay = (int) Duration.between(LocalTime.now(),
                fakeUpdateTime).toMillis() + 1000;
        scheduler.scheduleWithFixedDelay(() -> {
            timings.clear();
            //    timings.putAll(ApiCaller.getPrayerTimings("Alexandria","EG"));
            timings.putAll(ApiCaller.getDummyMap());
            homeForm.populateTable(timings);
            System.out.println("Map changed");
        }, timeUntilNextDay, Duration.ofDays(1).toMillis(), TimeUnit.MILLISECONDS);
        System.out.println(timeUntilNextDay / 1000 / 60);


    }


    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        Scheduler scheduler2 = new Scheduler();

        for (int i = 0; i < 4; i++) {
            scheduler.schedule(() -> System.out.println(3), 1 + i, TimeUnit.SECONDS);
            scheduler.schedule(() -> System.out.println(4), 1 + i, TimeUnit.SECONDS);
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