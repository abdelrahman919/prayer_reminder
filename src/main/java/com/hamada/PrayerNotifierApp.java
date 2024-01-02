package com.hamada;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

public class PrayerNotifierApp {
    private final static Map<String, String> PRAYER_TIMINGS = Map.ofEntries(
            Map.entry("Fajr", "05:25"),
            Map.entry("Sunrise", "06:59"),
            Map.entry("Dhuhr", "12:04"),
            Map.entry("Asr", "14:50"),
            Map.entry("Sunset", "17:09"),
            Map.entry("Maghrib", "17:09"),
            Map.entry("Isha", "18:33"),
            Map.entry("Imsak", "05:15"),
            Map.entry("Midnight", "00:04"),
            Map.entry("Firstthird", "21:46"),
            Map.entry("Lastthird", "02:22")
    );

    public static void main(String[] args) {
        // Create a Timer
        Timer timer = new Timer();

        // Schedule tasks for each prayer time
        for (Map.Entry<String, String> entry : PRAYER_TIMINGS.entrySet()) {
            String prayerName = entry.getKey();
            String prayerTime = entry.getValue();

            // Parse prayer time to calculate milliseconds until the next prayer
            long delayInMillis = calculateDelayInMillis(prayerTime);

            // Schedule the task to show a notification when it's time for the prayer
            timer.schedule(new PrayerNotificationTask(prayerName), new Date(System.currentTimeMillis() + delayInMillis));
        }
    }

    private static long calculateDelayInMillis(String prayerTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date prayerDateTime = sdf.parse(prayerTime);
            long currentTime = System.currentTimeMillis();
            long prayerTimeMillis = prayerDateTime.getTime();
            return prayerTimeMillis - currentTime;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    static class PrayerNotificationTask extends TimerTask {
        private final String prayerName;

        public PrayerNotificationTask(String prayerName) {
            this.prayerName = prayerName;
        }

        @Override
        public void run() {
            // Show a notification using JOptionPane
            JOptionPane.showMessageDialog(null, "It's time for " + prayerName + " prayer!", "Prayer Notification", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
