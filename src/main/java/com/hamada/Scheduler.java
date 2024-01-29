package com.hamada;

import com.hamada.gui.HomeForm;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    public static final List<String> sortedPrayers = new ArrayList<>(List.of("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"));

    //for testing
    public static LocalTime fakeUpdateTime = LocalTime.of(15,16,59,999999999);
//    public static LocalTime fakeUpdateTime = LocalTime.MAX;
    public static boolean isAdanScheduled;
    public static boolean isReminderScheduled;

    public static int period;

    private ScheduledExecutorService singleScheduler = Executors.newSingleThreadScheduledExecutor();

    public Scheduler() {
    }

    public ScheduledExecutorService getSingleScheduler() {
        return singleScheduler;
    }


    public void adanSchedule(Map<String, String> prayerTimings) {
        // WE create new instance of singleThreadedScheduler because if we shut the old down it can't schedule any more tasks
        // This poses threat of leaking the scheduled executor, so we never call this method when the scheduler is still running
        singleScheduler  = Executors.newSingleThreadScheduledExecutor();
        for (String prayer : prayerTimings.keySet()) {
            LocalTime nextPrayerTiming = LocalTime.parse(prayerTimings.get(prayer));
            if (nextPrayerTiming.isAfter(LocalTime.now())) {
                long timeUntilNextPrayer = Duration.between(LocalTime.now(), nextPrayerTiming).toMillis();
                singleScheduler.schedule(() -> MP3Player.play(MP3Player.adanPath),
                        timeUntilNextPrayer, TimeUnit.MILLISECONDS);
            }

        }
        singleScheduler.shutdown();
        isAdanScheduled = true;
    }

    public void reminderSchedule(Map<String, String> prayerTimings, int period) {
        singleScheduler = Executors.newSingleThreadScheduledExecutor();
        for (String prayer : prayerTimings.keySet()) {
            LocalTime nextPrayerTiming = LocalTime.parse(prayerTimings.get(prayer));
            if (nextPrayerTiming.isAfter(LocalTime.now())) {
                long timeUntilNextPrayer = Duration.between(LocalTime.now(), nextPrayerTiming).minusMinutes(period).toMillis();
                singleScheduler.schedule(() -> MP3Player.play(MP3Player.reminderPath),
                        timeUntilNextPrayer, TimeUnit.MILLISECONDS);
            }

        }
        singleScheduler.shutdown();
        isReminderScheduled = true;
    }



    //Updates timings map at the start of each day
    public static void updateTimings(Map<String, String> timings, HomeForm homeForm
            ,Scheduler adan, Scheduler reminder, Settings settings) {
        //Scheduler going to run at fixed delay "every day"
         ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        //Initial delay is the time left until the new day starts
        // 500ms is added to make sure we reached next day
        int timeUntilNextDay = (int) Duration.between(LocalTime.now(),
                LocalTime.MAX).toMillis()+500;
/*        int timeUntilNextDay = (int) Duration.between(LocalTime.now(),
                fakeUpdateTime).toMillis() + 1000;*/
        scheduler.scheduleWithFixedDelay(() -> {
            timings.clear();
            timings.putAll(Objects.requireNonNull(ApiCaller.getPrayerTimings()));
       //     timings.putAll(ApiCaller.getDummyMap());
            homeForm.populateTable();
            if (settings.isAdan()) {
                adan.adanSchedule(timings);
            }
            if (settings.isReminder()) {
                reminder.reminderSchedule(timings, settings.getPeriod());
            }
            System.out.println("Map changed");
        }, timeUntilNextDay, Duration.ofDays(1).toMillis(), TimeUnit.MILLISECONDS);
    //    System.out.println(timeUntilNextDay / 1000 / 60);


    }


    public static void main(String[] args) {

  /*      Scheduler scheduler = new Scheduler();
        Scheduler terminatedScheduler = new Scheduler();
        System.out.println(scheduler.singleScheduler.isTerminated());
        int outerI = 0;
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            scheduler.singleScheduler.schedule(() -> System.out.println(finalI),i+2,TimeUnit.SECONDS);
            outerI = i+2;
        }
        terminatedScheduler.singleScheduler.schedule(() -> scheduler.singleScheduler.schedule(() -> System.out.println("restated "), 0, TimeUnit.MILLISECONDS),
                outerI*1000+1000, TimeUnit.MILLISECONDS);
        scheduler.singleScheduler.shutdown();
        terminatedScheduler.singleScheduler.shutdown();
        System.out.println((scheduler.singleScheduler.isShutdown()));
        System.out.println(scheduler.singleScheduler.isTerminated());*/
        Scheduler scheduler = new Scheduler();
        scheduler.adanSchedule(ApiCaller.getDummyMap());
        System.out.println(scheduler.singleScheduler.shutdownNow());
    //    scheduler.adanSchedule(ApiCaller.getDummyMap());
        System.out.println(scheduler.singleScheduler.shutdownNow());
    //    scheduler.adanSchedule(ApiCaller.getDummyMap());
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