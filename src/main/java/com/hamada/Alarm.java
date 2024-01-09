package com.hamada;

import java.awt.*;

public class Alarm {

    static Runnable startAlarm = () -> {
        try {
            for (int i = 0; i < 5; i++) {
                Toolkit.getDefaultToolkit().beep();
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    };


    public static void main(String[] args) {
        try {
            for (int i = 0; i < 10; i++) {
                Toolkit.getDefaultToolkit().beep();
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

