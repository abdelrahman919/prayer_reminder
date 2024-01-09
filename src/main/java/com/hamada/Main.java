package com.hamada;

import com.hamada.gui.HomeForm;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args)  {

         Map<String,String>  prayerTimings = new HashMap<>();
        LocalDate date = null;
        List<String> sortedPrayers = new ArrayList<>(List.of("Fajr","Dhuhr","Asr","Maghrib","Isha"));

        //Try reading data from file
        String[] local_data = new String[0];
        try {
            //Separate today's date from the map of timings
            local_data = FileHandler.readFromFile().split("--");

            for (String currentString : local_data) {
                //if it starts with "{" then it's the map
                if (currentString.startsWith("{")) {
                    //Dividing bulk string into entry set parts
                    List<String> entrySets = Arrays.stream(currentString.replaceAll("[{}]", "")
                            .split(", ")).toList();
                    //Creating the map using a list of entry sets
                    prayerTimings = entrySets.stream().map(string -> string.split("="))
                            .collect(Collectors.toMap(strings -> strings[0], strings -> strings[1]));
                } else {
                    date = LocalDate.parse(currentString);
                }

            }
        } catch (Exception ignored) {
        }

        //If the date save in file and today's date are the same then the values read from file are valid for use today
        //If not then we  call the api to fetch the up-to-date date and save it along with today's date
        //This is to eliminate unnecessary api calls (even-though this one is free :D )
        if (!Objects.equals(date, LocalDate.now())) {
            prayerTimings = ApiCaller.getPrayerTimings();
            assert prayerTimings != null;
            System.out.println("API CALLED");
            FileHandler.writeToFile(prayerTimings.toString()+ "--" + LocalDate.now().toString());
        }



        Scheduler scheduler = new Scheduler();
        Scheduler scheduler2 = new Scheduler();
        TimeUnit unit = TimeUnit.SECONDS;
        TrayIcon trayIcon= MyTrayIcon.createTrayIcon();
        int period = 0;
        long delay = 0;
        long remainingTime = 0;
        //Calculate time delay for each prayer
        for (String prayer:sortedPrayers) {
            LocalTime currPrayerTime =LocalTime.parse(prayerTimings.get(prayer));
            //Check if prayer has already passed
            if (!currPrayerTime.isBefore(LocalTime.now())) {
                //If not calculate delay
                delay = Duration.between(LocalTime.now(),currPrayerTime).toSeconds() -(period*60);
                remainingTime = Duration.between(LocalTime.now(),currPrayerTime).toMinutes();
                System.out.println(" "+delay+" ");
                String message = String.format("%d Minutes left until %s ",remainingTime , prayer);
                //schedule an alarm with different delay for each prayer
                scheduler.schedule(
                        MyTrayIcon.displayTrayRunnable(message, trayIcon),
                        delay , unit);
                scheduler2.schedule(Alarm.startAlarm, delay, unit);
            }

        }

        scheduler.schedule(() -> {
            if (trayIcon != null) {
                SystemTray.getSystemTray().remove(trayIcon);
            }
        },delay,unit);
//        HomeForm homeForm = new HomeForm();


        //the delay in shutdown methods is the delay of last prayer +10
        System.out.println("Delay= "+delay);
        scheduler.shutdown();
        scheduler2.shutdown();









    }


}
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                MainForm.createAndShowGUI();
//            }
//        });