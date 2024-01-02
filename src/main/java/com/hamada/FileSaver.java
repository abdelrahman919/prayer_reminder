package com.hamada;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileSaver {

    public static void main(String[] args) {
        // Specify the file path
        Path filePath = Path.of("example.txt");

        // The text you want to write to the file
        String textToWrite = "Hello, World!\nThis is a sample text.";

        try {
            // Write the text to the file
            Files.writeString(filePath, textToWrite, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            System.out.println("Text has been written to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
