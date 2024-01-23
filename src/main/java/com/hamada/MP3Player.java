package com.hamada;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Period;

public class MP3Player {
    private String filePath;
    private AdvancedPlayer player;

    public MP3Player(String filePath) {
        this.filePath = filePath;
    }

    public void play() {
        LocalTime start = LocalTime.now();
        try {
            while (Duration.between(start,LocalTime.now()).toSeconds() < 5) {
                FileInputStream fileInputStream = new FileInputStream(filePath);
                Bitstream bitstream = new Bitstream(fileInputStream);
                int duration = bitstream.readFrame().max_number_of_frames((int) fileInputStream.getChannel().size());

                fileInputStream.close();

                fileInputStream = new FileInputStream(filePath);
                player = new AdvancedPlayer(fileInputStream);

                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        System.out.println("Playback finished");
                    }
                });

                Thread playerThread = new Thread(() -> {
                    try {
                        player.play(0, duration);
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                    }
                });

                playerThread.start();

                // Wait for the current player thread to finish before starting a new one
                playerThread.join();

                // Close the player and release resources
                player.close();
            }
        } catch (JavaLayerException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // Specify the path to your MP3 file
        String mp3FilePath = "C:\\Users\\abdelrahman\\Downloads\\beep.Mp3";

        MP3Player mp3Player = new MP3Player(mp3FilePath);
        mp3Player.play();
    }
}


//      "C:\\Users\\abdelrahman\\Downloads\\beep.MP3"