package com.hamada;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Test {

    static List<String> messages = new ArrayList<>(List.of("sb7111","sb72222","sb7333"));

    public static void main(String[] args) throws AWTException, ExecutionException, InterruptedException {
        int n= 3;
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
        scheduler2.shutdown();






    }




}
