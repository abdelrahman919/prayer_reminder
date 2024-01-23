package com.hamada.gui;

import com.hamada.ApiCaller;
import com.hamada.Scheduler;
import com.hamada.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class HomeForm extends JFrame {
    private JPanel homePage;
    private JTable prayerTimeTable;
    private JButton settignsButton;
    private JButton button2;
    private JScrollPane tableScrollPane;
    private JLabel prayerTimeLabel;
    private JPanel innerPane;
    private JToolBar bar;
    private JToolBar.Separator buttonSeparator;
    private JLabel timerLabel;
    private JLabel nextPrayerLabel;
    private JLabel locationLabel;
    private JLabel clockLabel;
    private Timer timer;
    private Timer clockTimer;
    Timer prayerTimer;

    static List<String> sortedPrayers = new ArrayList<>(List.of("Fajr","Dhuhr","Asr","Maghrib","Isha"));


    public HomeForm() {
        setContentPane(homePage);
        setTitle("HomePage");
        setSize(500,450);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);

        DefaultTableModel defaultTableModel = new DefaultTableModel();
        prayerTimeTable.setModel(defaultTableModel);
        prayerTimeTable.setRowHeight(35);


        settignsButton.addActionListener(e -> SwingUtilities.invokeLater(SettingsFrom::new));
    }

    //Formats prayer timings to 12H format
    public String timeFormatter(String time){
        return DateTimeFormatter.ofPattern("hh:mm a").format(DateTimeFormatter.ofPattern("HH:mm").parse(time));
    }


    //Populate table with data from the map and do some representational changes
    public void populateTable( Map<String,String> map) {
    //    Object[][] data = map.entrySet().stream().map(entry -> new String[]{entry.getKey(), entry.getValue()}).toArray(Object[][]::new);

        //get values of prayers and timings in ordered manner and map it to an Object[][] to populate the table
        Object[][] data = sortedPrayers.stream().map(prayer -> new String[]{prayer ,timeFormatter(map.get(prayer))}).toArray(Object[][]::new);

        DefaultTableModel model = new DefaultTableModel(data,
                new String[]{"Prayer", "Time"});
        prayerTimeTable.setModel(model);
        model.fireTableDataChanged();
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        prayerTimeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        prayerTimeTable.getColumnModel().getColumn(1).setCellRenderer(renderer);
    }


    //Format the duration between now and next prayer into mm:ss format
    public Duration setTimerText(LocalTime time) {
        Duration currTime = Duration.between(LocalTime.now(), time);
        long HH = currTime.toHours();
        long MM = currTime.toMinutesPart() +1;
        String timeInHHMMSS = String.format("%02d:%02d", HH, MM);
        timerLabel.setText(timeInHHMMSS);
        return currTime;
    }

    //Count down timer for next prayer
    public void prayerTimer(LocalTime time, String prayer) {
        nextPrayerLabel.setText(prayer+" in:");
        setTimerText(time);

        timer = new Timer(0, e -> {

            int delay = (60-LocalTime.now().getSecond())*1000;
            timer.setDelay(delay);
            System.out.println(delay/1000);
            Duration currTime = setTimerText(time);

            if (currTime.isZero() || currTime.isNegative()) {
                timer.stop();
                timerLabel.setText("00:00:00");
            }

        });
        timer.setInitialDelay((60-LocalTime.now().getSecond())*1000);
        timer.start();
    }



    public void setLocationLabel(String location) {
        locationLabel.setText(location);
    }

    public void startClock() {
        SimpleDateFormat clockFormat12H = new SimpleDateFormat("hh:mm a");
        clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));

        clockTimer = new Timer(0
                ,e -> {
            clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
            clockTimer.setDelay(60000);
        });
        clockTimer.setInitialDelay((60-LocalTime.now().getSecond())*1000);
        clockTimer.start();

    }

    //Returns next prayer if there's none returns empty string
    public String getNextPrayer(Map<String,String> timings) {
        String startingPrayer = "";
        final List<String> sortedPrayers = Scheduler.sortedPrayers;
        for (String prayer : sortedPrayers) {
            if (LocalTime.parse(timings.get(prayer)).isAfter(LocalTime.now())) {
                startingPrayer = prayer;
                break;
            }
        }
        return startingPrayer;
    }


//    public void prayerTimer2(AtomicReference<Map<String,String>> timingsRef) {
    public void prayerTimer2(Map<String,String>timings) {
    //    Map<String,String> timings = timingsRef.get();

        final String startingPrayer = getNextPrayer(timings);

        if (!startingPrayer.isEmpty()) {
            nextPrayerLabel.setText(startingPrayer);
            LocalTime time = LocalTime.parse(timings.get(startingPrayer));
            setTimerText(time);
        }
        prayerTimer = new Timer(0, e -> {
            String currentPrayer = getNextPrayer(timings);
        //    System.out.println(currentPrayer);
            if (!currentPrayer.isEmpty()) {
                nextPrayerLabel.setText(currentPrayer);
                LocalTime currentTime = LocalTime.parse(timings.get(currentPrayer));
                setTimerText(currentTime);
                prayerTimer.setDelay(60000);
            }else {
                timerLabel.setText("");
                nextPrayerLabel.setText("");
                prayerTimer.setInitialDelay((int) Duration.between(LocalTime.now(),
                        Scheduler.fakeUpdateTime).toMillis()+3000);
/*
                        prayerTimer.setInitialDelay((int) Duration.between(LocalTime.now(),
                        LocalTime.MAX).toMillis()+1000);
*/

                System.out.println(prayerTimer.getInitialDelay()/60000);
                prayerTimer.restart();
//                Scheduler.updateTimings(timings);
/*                changeMap(timings);
                System.out.println("inside: "+ timings);
                populateTable(timings);
                nextPrayerLabel.setText(getNextPrayer(timings));
                setTimerText(LocalTime.parse(timings.get(startingPrayer)));*/
                /*prayerTimer.stop();
                timerLabel.setText("");
                nextPrayerLabel.setText("");*/
            }

        });
    //    prayerTimer.setInitialDelay((60-LocalTime.now().getSecond())*1000);
        prayerTimer.start();


    }

    public Map<String, String> changeMap(Map<String, String> mapToChange) {
        mapToChange.clear();
        mapToChange.putAll(ApiCaller.getDummyMap());
        System.out.println(mapToChange);
        return mapToChange;
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String city = "Alex";
            String country = "EG";

            HomeForm homeForm = new HomeForm();
            homeForm.locationLabel.setText("Alex,EG");

            Map<String, String> prayerTimingsMapUnModifiable = Map.ofEntries(
                    Map.entry("Fajr", "11:30")
                    ,Map.entry("Dhuhr", "11:31"),
                    Map.entry("Asr", "11:31"),
                    Map.entry("Maghrib", "11:31"),
                    Map.entry("Isha", "11:00")
            );
            HashMap<String, String> prayerTimingsMap = new HashMap<>(prayerTimingsMapUnModifiable);

        //    AtomicReference<Map<String,String>> timingsRef = new AtomicReference<>(prayerTimingsMap);


            // Populate the table with the sample data
        //    homeForm.populateTable(timingsRef.get());
        //    homeForm.populateTable(timingsRef.get());
            Scheduler.updateTimings(prayerTimingsMap,homeForm);
            homeForm.populateTable(prayerTimingsMap);
            homeForm.startClock();



        //    homeForm.prayerTimer2(timingsRef);
            homeForm.prayerTimer2(prayerTimingsMap);



/*            long delay = 0;
            Scheduler scheduler= new Scheduler();
            int counter =0;
            for (String prayer : sortedPrayers) {
                LocalTime prayerTime = LocalTime.parse(prayerTimingsMap.get(prayer));
                if(!prayerTime.isBefore(LocalTime.now())){
                    scheduler.schedule(() -> homeForm.prayerTimer(prayerTime,prayer),
                            1100+delay, TimeUnit.MILLISECONDS);
                //    System.out.println(prayer + ": " +delay);
                //    System.out.println("Timer: "+prayerTime);
                    counter++;
                    delay = Duration.between(LocalTime.now(), prayerTime).toMillis();
                }

            }
            scheduler.shutdown();*/





/*            LocalTime tstTime =LocalTime.parse("14:06");
            LocalTime tstTime2 =LocalTime.parse("14:07");
                    homeForm.prayerTimer(tstTime);
            long delay = Duration.between(LocalTime.now(),tstTime).toSeconds();
            Scheduler scheduler = new Scheduler();
            scheduler.schedule(() -> homeForm.prayerTimer(tstTime2),delay+3,TimeUnit.SECONDS);*/


        });


    }


}

