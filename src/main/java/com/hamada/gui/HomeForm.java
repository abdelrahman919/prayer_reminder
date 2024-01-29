package com.hamada.gui;

import com.hamada.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HomeForm extends JFrame {
    private JPanel homePage;
    private JTable prayerTimeTable;
    private JButton settignsButton;
    private JButton stopAudioButton;
    private JScrollPane tableScrollPane;
    private JLabel prayerTimeLabel;
    private JPanel innerPane;
    private JToolBar bar;
    private JToolBar.Separator buttonSeparator;
    private JLabel timerLabel;
    private JLabel nextPrayerLabel;
    private JLabel locationLabel;
    private JLabel clockLabel;
    private JLabel adanStatusLabel;
    private JLabel reminderStatusLabel;
    private Timer timer;
    private Timer clockTimer;
    Timer prayerTimer;
    Settings settings;
    Map<String, String> prayerTimings;




    static List<String> sortedPrayers = new ArrayList<>(List.of("Fajr","Dhuhr","Asr","Maghrib","Isha"));


    public HomeForm(Map<String, String> prayerTimings, Settings settings,
    Scheduler adanScheduler, Scheduler reminderScheduler ) {
        setContentPane(homePage);
        setTitle("HomePage");
        setSize(500, 450);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        this.settings = settings;
        loadSettingsStatus();
        this.prayerTimings = prayerTimings;

        DefaultTableModel defaultTableModel = new DefaultTableModel();
        prayerTimeTable.setModel(defaultTableModel);
        prayerTimeTable.setRowHeight(35);

        // This code will be executed after the SettingsForm is closed to SYNC any changes
        //This eliminates the need to restart the app after changing the settings
        settignsButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            new SettingsFrom(settings).addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    //Refreshing home form displayed settings
                    loadSettingsStatus();
                    //If location is changed, change the timings
                    if (!Objects.equals(ApiCaller.getCountry(), settings.getCountry()) ||
                            !Objects.equals(ApiCaller.getCity(),settings.getCity())) {
                        updateLocationAndSaveMap(settings);
                    }
                    //IF adan scheduler was on the stopped
                    if (!settings.isAdan() && Scheduler.isAdanScheduled) {
                        adanScheduler.getSingleScheduler().shutdownNow();
                        //Keeping value in sync with settings
                        //to compare with settings values after they are changed
                        Scheduler.isAdanScheduled = false;
                    }
                    //IF adan was off then turned on
                    //Check if adan is already scheduled, so we don't create new schedulers
                    else if (settings.isAdan() && !Scheduler.isAdanScheduled) {
                        adanScheduler.adanSchedule(prayerTimings);
                    }
                    //Same as the above but for reminder
                    if (!settings.isReminder() && Scheduler.isReminderScheduled) {
                        reminderScheduler.getSingleScheduler().shutdownNow();
                        Scheduler.isReminderScheduled = false;
                    }
                    //IF reminder was off then turned on
                    else if (settings.isReminder() && !Scheduler.isReminderScheduled) {
                        reminderScheduler.reminderSchedule(prayerTimings,settings.getPeriod());
                    }
                    //IF reminder period changes while the scheduler is on
                    else if (settings.isReminder() && Scheduler.period != settings.getPeriod() ) {
                        //Shut current scheduler down first
                        reminderScheduler.getSingleScheduler().shutdownNow();
                        //Start new one with the new period
                        reminderScheduler.reminderSchedule(prayerTimings, settings.getPeriod());
                        //Sync the two period values for future comparisons
                        Scheduler.period = settings.getPeriod();
                    }

                    System.out.println("SettingsForm closed");
                }
            });
        }));
        showStopAudioButton();
        stopAudioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MP3Player.stopPlayer();
            }
        });
    }

    public void loadSettingsStatus() {
        locationLabel.setText(settings.getCity() + ", " + settings.getCountry());
        adanStatusLabel.setText(settings.isAdan() ? "ON" : "OFF");
        reminderStatusLabel.setText(settings.isReminder() ? "ON, " + settings.getPeriod() + "Min" : "OFF");
    }

    //If location settings change update the timings and location settings
    public void updateLocationAndSaveMap(Settings settings) {
            ApiCaller.setCountry(settings.getCountry());
            ApiCaller.setCity(settings.getCity());
            prayerTimings.clear();
            prayerTimings.putAll(Objects.requireNonNull(ApiCaller.getPrayerTimings()));
            populateTable();
            prayerTimer.setInitialDelay(0);
            prayerTimer.restart();
            FileHandler.writeToFile(prayerTimings.toString() + "\n" + LocalDate.now().toString(), FileHandler.prayerFilePath);
    }

    public void showStopAudioButton() {
        Timer stopAudioButtonTimer = new Timer(0,e -> {
            if (MP3Player.getPlayerThread() != null && MP3Player.getPlayerThread().isAlive()) {
                if (!stopAudioButton.isVisible()) {
                    stopAudioButton.setVisible(true);
                }
            } else {
                if (stopAudioButton.isVisible()) {
                    stopAudioButton.setVisible(false);
                }
            }

        });
        stopAudioButtonTimer.setDelay(1000);
        stopAudioButtonTimer.start();
    }

    //Formats prayer timings to 12H format to display in table
    public String timeFormatter(String time){
        return DateTimeFormatter.ofPattern("hh:mm a").format(DateTimeFormatter.ofPattern("HH:mm").parse(time));
    }


    //Populate table with data from the map and do some representational changes
    public void populateTable() {
        Object[][] data = new Object[0][];
        //get values of prayers and timings in ordered manner and map it to an Object[][] to populate the table
        try {
            data = sortedPrayers.stream().map(prayer -> new String[]{prayer, timeFormatter(prayerTimings.get(prayer))}).toArray(Object[][]::new);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(this,"Error fetching prayer timings possibly " +
                    "due to invalid location input or network connection error ");

        }

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
    public void setTimerText(LocalTime time) {
        Duration currTime = Duration.between(LocalTime.now(), time);
        long HH = currTime.toHours();
        long MM = currTime.toMinutesPart() +1;
        String timeInHHMMSS = String.format("%02d:%02d", HH, MM);
        timerLabel.setText(timeInHHMMSS);
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


    //Show time until next prayers ticking by the minute
    public void prayerTimer2(Map<String,String>timings) {

        //this code is executed at once displaying timer's initial values
        final String startingPrayer = getNextPrayer(timings);
        if (!startingPrayer.isEmpty()) {
            nextPrayerLabel.setText(startingPrayer);
            LocalTime time = LocalTime.parse(timings.get(startingPrayer));
            setTimerText(time);
        }
        //this timer starts later at the star of the new minute(synced with system clock)
        prayerTimer = new Timer(0, e -> {
            String currentPrayer = getNextPrayer(timings);
            if (!currentPrayer.isEmpty()) {
                nextPrayerLabel.setText(currentPrayer);
                LocalTime currentTime = LocalTime.parse(timings.get(currentPrayer));
                setTimerText(currentTime);
                prayerTimer.setDelay(60000);
            }else {
                //Meaning we passed Isha so no more prayers today
                timerLabel.setText("");
                nextPrayerLabel.setText("");
                //Restart the timer tomorrow after the prayers map has been updated
/*                prayerTimer.setInitialDelay((int) Duration.between(LocalTime.now(),
                        Scheduler.fakeUpdateTime).toMillis()+3000);*/
                        prayerTimer.setInitialDelay((int) Duration.between(LocalTime.now(),
                        LocalTime.MAX).toMillis()+3000);

            //    System.out.println(prayerTimer.getInitialDelay()/60000);
                prayerTimer.restart();

            }

        });
        //Calculating time until next minute to sync timer with system clock
        prayerTimer.setInitialDelay((60-LocalTime.now().getSecond())*1000);
        prayerTimer.start();

    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            Settings settings1 = FileHandler.readSettings();

            Map<String, String> prayerTimingsMapUnModifiable = Map.ofEntries(
                    Map.entry("Fajr", "00:34")
                    ,Map.entry("Dhuhr", "14:56"),
                    Map.entry("Asr", "14:57"),
                    Map.entry("Maghrib", "14:58"),
                    Map.entry("Isha", "22:00")
            );
            HashMap<String, String> prayerTimingsMap = new HashMap<>(prayerTimingsMapUnModifiable);

            Scheduler adanScheduler = new Scheduler();
            Scheduler reminderScheduler = new Scheduler();
            HomeForm homeForm = new HomeForm(prayerTimingsMap, settings1, adanScheduler, reminderScheduler);
            //    AtomicReference<Map<String,String>> timingsRef = new AtomicReference<>(prayerTimingsMap);

            adanScheduler.adanSchedule(prayerTimingsMap);
            reminderScheduler.reminderSchedule(prayerTimingsMap,settings1.getPeriod());

            // Populate the table with the sample data
        //    homeForm.populateTable(timingsRef.get());
        //    homeForm.populateTable(timingsRef.get());

        //    Scheduler.updateTimings(prayerTimingsMap,homeForm);
            homeForm.populateTable();
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

