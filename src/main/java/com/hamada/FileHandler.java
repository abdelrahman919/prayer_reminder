package com.hamada;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileHandler {

    public final static Path prayerFilePath = Paths.get(".", "local_data.txt");
    public final static Path settingsFilePath = Paths.get(".", "settings.txt");

     static public String readFromFile(Path filePath){
         try {
             return Files.readString(filePath);
         }catch (IOException e){e.printStackTrace();}
         return null;
     }


    static public void writeToFile(String textToWrite, Path filePath){
        try {
            // Write the text to the file
            Files.writeString(filePath, textToWrite , StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("Text has been written to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public Settings readSettings() {
        Settings settings = new Settings();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(String.valueOf(FileHandler.settingsFilePath)))) {
            settings = (Settings) ois.readObject();
            System.out.println("Settings data loaded");
        } catch (FileNotFoundException e) {
            System.err.println("Settings file not found: " + e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error while reading settings: " + e.getMessage());
        }

        return settings;
    }

    static public void saveSettings(Settings settings) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(String.valueOf(FileHandler.settingsFilePath)))) {
            oos.writeObject(settings);
            System.out.println("Settings saved ") ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }











    public static void main(String[] args) throws IOException {
        // Specify the file path

/*        writeToFile(TimingsReminder.prayerTimingsMap.toString()+"--"+ LocalDate.now().toString(),prayerFilePath);

        String data= readFromFile(prayerFilePath);*/
        String city = "cairo";

        //    List<String> data = Files.readAllLines(settingsFilePath);
        //    data.replaceAll(string -> string.contains("city") ? String.format("city:%s", city) : string);
        /*data.stream().filter(string -> string.contains("city"))
                .map(string -> string.replaceAll(string, String.format("city:%s", city))).toList();*/
        String data = Files.readString(settingsFilePath);
        String countryTextField= "SA";
        data = Arrays.stream(data.split("\n")).map(string -> string.contains("country") ?
                "country:" + countryTextField : string).collect(Collectors.joining("\n"));

//        String strToWrite = String.join("\n",newData);
  //      Files.writeString(settingsFilePath,strToWrite);
        System.out.println(data.length());
        System.out.println(data);




        String inputTime24H = "14:10"; // Example time in 24-hour format

        // Create a SimpleDateFormat object for formatting in 12-hour time
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");

        try {
            // Format the input time directly in 12-hour format
            String outputTime12H = outputFormat.format(new SimpleDateFormat("hh:mm").parse(inputTime24H));

            // Print the result
            System.out.println("Input Time (24H): " + inputTime24H);
            System.out.println("Converted Time (12H): " + outputTime12H);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(Duration.between(LocalTime.now(),LocalTime.now().plusMinutes(1).withSecond(0).withNano(0)).toMillis() / 1000);

    }
}
