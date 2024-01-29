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
    Settings settings;


    public static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public SettingsFrom(Settings settings) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        this.settings = settings;
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
        scheduleAdanRadioButton.setSelected(settings.isAdan());
        scheduleReminderRadioButton.setSelected(settings.isReminder());
    }

    private void onOK() {
        // Sync any settings change and save to file
        settings.setAdan(scheduleAdanRadioButton.isSelected());
        settings.setReminder(scheduleReminderRadioButton.isSelected());
        if (!countryTextField.getText().isEmpty() && !isNumeric(countryTextField.getText())) {
            settings.setCountry(countryTextField.getText().strip());
        }
        if (!cityTextField.getText().isEmpty() && !isNumeric(cityTextField.getText())) {
            settings.setCity(cityTextField.getText().strip());
        }
        if (comboBoxChanged) {
            settings.setPeriod(Integer.parseInt((String) comboBox1.getSelectedItem()));
        }
        FileHandler.saveSettings(settings);
        dispose();

    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        //    SettingsFrom dialog = new SettingsFrom();
      /*  dialog.pack();
        dialog.setVisible(true);*/

        System.exit(0);
        //    SwingUtilities.invokeLater(SettingsFrom::new);
    }
}
