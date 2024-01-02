package com.hamada;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class MyTrayIcon {
    public java.awt.TrayIcon createTrayIcon() throws AWTException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

        java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);
        return trayIcon;

    }

    public void displayTray(String message, TrayIcon trayIcon){
        trayIcon.displayMessage("Prayer Reminder", """
        %s
        """.formatted(message), MessageType.INFO);
    }

    public Runnable displayTrayRunnable(String message,TrayIcon trayIcon){
        return () -> displayTray(message,trayIcon);
    }



    public static void main(String[] args) throws AWTException {
        if (SystemTray.isSupported()) {
            MyTrayIcon td = new MyTrayIcon();
            java.awt.TrayIcon trayIcon = td.createTrayIcon();

            td.displayTray("Sb7",trayIcon);

            Runnable trayTask = () -> {
                td.displayTray("SBA7OOOO",trayIcon);
            };
            Scheduler scheduler = new Scheduler();
            scheduler.schedule(trayTask,5L,TimeUnit.SECONDS);

            scheduler.shutdown(5);
        } else {
            System.err.println("System tray not supported!");
        }

    }

//    public java.awt.TrayIcon createTrayIcon() throws AWTException {
//        //Obtain only one instance of the SystemTray object
//        SystemTray tray = SystemTray.getSystemTray();
//
//        //If the icon is a file
//        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
//        //Alternative (if the icon is on the classpath):
//        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));
//
//        java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image, "Tray Demo");
//        //Let the system resize the image if needed
//        trayIcon.setImageAutoSize(true);
//        //Set tooltip text for the tray icon
//        trayIcon.setToolTip("System tray icon demo");
//        tray.add(trayIcon);
//        return trayIcon;
//
//    }
//
//    public void displayTray(String message, java.awt.TrayIcon trayIcon){
//        trayIcon.displayMessage("Prayer Reminder", """
//        %s
//        """.formatted(message), MessageType.INFO);
//    }


}