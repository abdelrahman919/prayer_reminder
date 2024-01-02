package com.hamada;

import java.util.Map;

public class TimingsReminder {
    Map<String, String> prayerTimingsMap = Map.ofEntries(
            Map.entry("Fajr", "05:25"),
            Map.entry("Dhuhr", "12:04"),
            Map.entry("Asr", "14:50"),
            Map.entry("Maghrib", "17:09"),
            Map.entry("Isha", "18:33")
    );




    public static void main(String[] args) {
        Map<String, String> prayerTimingsMap = Map.ofEntries(
                Map.entry("Fajr", "05:25"),
                Map.entry("Dhuhr", "12:04"),
                Map.entry("Asr", "14:50"),
                Map.entry("Maghrib", "17:09"),
                Map.entry("Isha", "18:33")
        );



        System.out.println(prayerTimingsMap);
    }
}
