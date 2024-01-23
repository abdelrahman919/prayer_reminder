package com.hamada.gui;

import com.hamada.FileHandler;
import com.hamada.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.EventListener;
import java.util.Objects;

public class SettingsFrom extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField countryTextField;
    private JTextField cityTextField;
    private JRadioButton scheduleAdanRadioButton;
    private JRadioButton scheduleReminderRadioButton;
    private JComboBox<Integer> comboBox1;
    boolean adanSelected;
    boolean reminderSelected;
    boolean comboBoxChanged;
    Settings settings ;




    public SettingsFrom() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        loadSettingsStatus();

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        //Changes the value of adan in file between TRUE and FALSE to match the button
        scheduleAdanRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adanSelected = true;
            }
        });

        //Changes the value of reminder in file between TRUE and FALSE to match the button
        scheduleReminderRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reminderSelected = true;
            }
        });

        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBoxChanged = true;
            }
        });
        comboBox1.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                comboBoxChanged = true;
            }
        });
        setVisible(true);
    }

    //Loads the status of Radio buttons (whether selected or not) from file to avoid confusion
    //If the file doesn't exist or any error occurs it writes default values to the file and uses them
    private void loadSettingsStatus() {
        String status = FileHandler.readFromFile(FileHandler.settingsFilePath);
        try {
            boolean adanStatus = Arrays.stream(status.split("\n")).filter(string -> string.contains("adan"))
                    .map(string -> string.contains("true")).findAny().get();
            boolean reminderStatus = Arrays.stream(status.split("\n")).filter(string -> string.contains("reminder"))
                    .map(string -> string.contains("true")).findAny().get();

            scheduleAdanRadioButton.setSelected(adanStatus);
            scheduleReminderRadioButton.setSelected(reminderStatus);
        }catch (Exception e){
            status = """
                    city:Alexandria
                    country:Egypt
                    adan:true
                    reminder:true
                    period:15""";
            FileHandler.writeToFile(status, FileHandler.settingsFilePath);
            scheduleAdanRadioButton.setSelected(true);
            scheduleReminderRadioButton.setSelected(true);
        }


    }


    private void cleanup() {
        buttonOK.removeActionListener(buttonOK.getActionListeners()[0]);
        buttonCancel.removeActionListener(buttonCancel.getActionListeners()[0]);
        scheduleAdanRadioButton.removeActionListener(scheduleAdanRadioButton.getActionListeners()[0]);
        scheduleReminderRadioButton.removeActionListener(scheduleReminderRadioButton.getActionListeners()[0]);
        comboBox1.removeActionListener(comboBox1.getActionListeners()[0]);
        comboBox1.removePropertyChangeListener(comboBox1.getPropertyChangeListeners()[0]);
    }


    private void onOK() {
        // add your code here
        if (adanSelected || reminderSelected || comboBoxChanged ||
        !countryTextField.getText().isEmpty() || !cityTextField.getText().isEmpty()) {
            String settings = FileHandler.readFromFile(FileHandler.settingsFilePath);
            assert settings != null;
            String adan = "";
            String reminder = "";
            String country = "";
            String city = "";
            String period = "";

            for (String setting : settings.split("\n")) {
                if (setting.contains("adan")) {
                    adan = setting;
                } else if (setting.contains("reminder")) {
                    reminder = setting;
                } else if (setting.contains("country")) {
                    country = setting;
                } else if (setting.contains("city")) {
                    city = setting;
                } else if (setting.contains("period")) {
                    period = setting;
                }
            }
            if (reminderSelected) {
                if (scheduleReminderRadioButton.isSelected()) {
                    reminder = "reminder: true";
                } else {
                    reminder = "reminder: false";
                }
                reminderSelected = false;
            }

            if (adanSelected) {
                if (scheduleAdanRadioButton.isSelected()) {
                    adan = "adan: true";
                } else {
                    adan = "adan: false";
                }
                adanSelected = false;
            }

            if (countryTextField.getText() != null && !countryTextField.getText().isEmpty()) {
/*            settings= Arrays.stream(settings.split("\n")).map(string -> string.contains("country") ?
                    "country:" + countryTextField.getText() : string).collect(Collectors.joining("\n"));
            FileHandler.writeToFile(settings,FileHandler.settingsFilePath);*/
                country = "country:" + countryTextField.getText();
            }

            if (cityTextField != null && !cityTextField.getText().isEmpty()) {
                city = "city:" + cityTextField.getText();
            }

            if (comboBoxChanged) {
                period = "period:" + Objects.requireNonNull(comboBox1.getSelectedItem());
            }
            FileHandler.writeToFile(String.format("""
                    %s
                    %s
                    %s
                    %s
                    %s""", reminder, adan, country, city, period), FileHandler.settingsFilePath);
        }
        cleanup();
        dispose();

    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        SettingsFrom dialog = new SettingsFrom();
      /*  dialog.pack();
        dialog.setVisible(true);*/

        System.exit(0);
    //    SwingUtilities.invokeLater(SettingsFrom::new);
    }
}
