package com.hamada;

import com.hamada.gui.HomeForm;

import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class Main {



    public static void main(String[] args)  {
        Map<String,String>  prayerTimings = new HashMap<>();
        LocalDate date ;
        Settings settings = FileHandler.readSettings();
        Scheduler adanScheduler = new Scheduler();
        Scheduler reminderScheduler = new Scheduler();

        //Syncing classes with current settings
        Scheduler.isAdanScheduled = settings.isAdan();
        Scheduler.isReminderScheduled = settings.isReminder();
        Scheduler.period = settings.getPeriod();
        ApiCaller.setCity(settings.getCity());
        ApiCaller.setCountry(settings.getCountry());


        //Try reading data from file
        String[] local_data = new String[0];
        try {
            //Separate today's date from the map of timings
            local_data = FileHandler.readFromFile(FileHandler.prayerFilePath).split("\n");

            for (String currentString : local_data) {

                //if the string starts with "{" then it's the map if not then it's the date
                if (!currentString.startsWith("{")) {
                    date = LocalDate.parse(currentString);
                    //If the date save in file isn't of today's date then it's outdated
                    //So we  call the api to fetch the up-to-date data and save it
                    // This is to eliminate unnecessary api calls (even-though this one is free :D )
                    if (!Objects.equals(date, LocalDate.now())) {
                        prayerTimings = ApiCaller.getPrayerTimings();
                        if (prayerTimings != null) {
                            System.out.println("API CALLED");
                            FileHandler.writeToFile(prayerTimings.toString() + "\n" + LocalDate.now().toString(), FileHandler.prayerFilePath);
                            break;
                        }
                    }

                } else {
                    //Means it's up-to-date, so we can use the map in file
                    //Dividing bulk string into entry set parts
                    List<String> entrySets = Arrays.stream(currentString.replaceAll("[{}]", "")
                            .split(", ")).toList();
                    //Creating the map using a list of entry sets
                    prayerTimings = entrySets.stream().map(string -> string.split("="))
                            .collect(Collectors.toMap(strings -> strings[0], strings -> strings[1]));
                }

            }
        } catch (Exception ignored) {
        }






        HomeForm homeForm = new HomeForm(prayerTimings, settings, adanScheduler, reminderScheduler);
        homeForm.populateTable();
        homeForm.startClock();
        homeForm.prayerTimer2(prayerTimings);
        if (settings.isAdan() && prayerTimings != null) {
            adanScheduler.adanSchedule(prayerTimings);
        }

        if (settings.isReminder() && prayerTimings != null) {
            reminderScheduler.reminderSchedule(prayerTimings, settings.getPeriod());
        }



        Scheduler.updateTimings(prayerTimings,homeForm,adanScheduler,reminderScheduler,settings);













    }


}
