package com.hamada;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class FileHandler {

    final static Path filePath = Path.of("D:\\java-projects\\prayer_reminder\\local_data.txt");

     static public String readFromFile(){
         try {
             return Files.readString(filePath);
         }catch (IOException e){e.printStackTrace();}
         return null;
     }


    static public void writeToFile(String textToWrite ){
        try {
            // Write the text to the file
            Files.writeString(filePath, textToWrite , StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("Text has been written to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws IOException {
        // Specify the file path

        writeToFile(TimingsReminder.prayerTimingsMap.toString()+"--"+ LocalDate.now().toString());

        String data= readFromFile();
        System.out.println((data.length()));
        System.out.println(data);


    }
}
