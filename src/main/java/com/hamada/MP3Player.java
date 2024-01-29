package com.hamada;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Period;
import java.util.Timer;
import java.util.TimerTask;

public class MP3Player {
    public static final String adanPath = String.valueOf(Paths.get(".", "sound", "adan.mp3"));
    public static final String reminderPath = String.valueOf(Paths.get(".", "sound", "reminder.mp3"));
    private static AdvancedPlayer player;
    private static volatile boolean running;

    private static Thread playerThread;


    static public void play(String filePath) {
        LocalTime start = LocalTime.now();
        running = true;

        try {
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

            playerThread = new Thread(() -> {
                try {
                    player.play(0, duration);
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            });

            playerThread.start();

        } catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopPlayer() {
        running = false;
        if (player != null) {
            player.close();
            System.out.println("player closed");
        }
    }

    public static AdvancedPlayer getPlayer() {
        return player;
    }

    public static void setPlayer(AdvancedPlayer player) {
        MP3Player.player = player;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        MP3Player.running = running;
    }

    public static Thread getPlayerThread() {
        return playerThread;
    }

    public static void setPlayerThread(Thread playerThread) {
        MP3Player.playerThread = playerThread;
    }

    public static void main(String[] args) {

        System.out.println(player);

        play(adanPath);
        System.out.println(player);
        //   stopPlayer();
        LocalTime end = LocalTime.now().plusSeconds(3);
        while (true) {
            if (Duration.between(LocalTime.now(), end).isNegative()) {
                stopPlayer();
                System.out.println("hi");
                break;
            }

        }

    }
}


//      "C:\\Users\\abdelrahman\\Downloads\\beep.MP3"