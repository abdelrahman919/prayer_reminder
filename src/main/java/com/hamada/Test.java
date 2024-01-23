package com.hamada;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Test {

    static List<String> messages = new ArrayList<>(List.of("sb7111","sb72222","sb7333"));
    public Map<String, String> changeMap(Map<String, String> mapToChange) {
        mapToChange.clear();
        mapToChange.putAll(ApiCaller.getDummyMap());

        System.out.println(mapToChange);
        return mapToChange;
    }

    public static void main(String[] args) throws AWTException, ExecutionException, InterruptedException {
/*        int n= 3;
        long delay = 15L;
        TimeUnit unit = TimeUnit.SECONDS;
        MyTrayIcon myTrayIcon = new MyTrayIcon();
        Scheduler scheduler = new Scheduler();
        Scheduler scheduler2 = new Scheduler();

        TrayIcon trayIcon= myTrayIcon.createTrayIcon();

        ScheduledFuture<?> previousTask = null;
        ScheduledFuture<?> previousAlarm = null;
        for (int i = 0; i < n; i++) {
            if (previousTask != null && previousAlarm != null) {
                // Wait for the completion of the previous task
                previousTask.get();
                previousAlarm.get();
            }

            // Schedule the next task
            ScheduledFuture<?> currentTask = scheduler.schedule(
                    myTrayIcon.displayTrayRunnable(messages.get(i), trayIcon),
                    delay , unit);
            ScheduledFuture<?> currentAlarm = scheduler2.schedule(Alarm.startAlarm, delay, unit);

            // Update the previous task reference
            previousTask = currentTask;
            previousAlarm = currentAlarm;

        }

        // Shut down the scheduler
        scheduler.shutdown();
        scheduler2.shutdown();*/
    ////////////////////////////////////////////////

        System.out.println(LocalTime.of(23, 16, 59, 999999999).plus(Duration.ofMillis(1000)));





    }




}
