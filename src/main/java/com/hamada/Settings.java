package com.hamada;

import java.io.Serializable;

public class Settings implements Serializable {
    private String country ;
    private String city ;
    private boolean adan;
    private boolean reminder;
    private int period;

    public Settings() {
        this.country = "egypt";
        this.city = "alexandria";
        this.adan = true;
        this.reminder = true;
        this.period = 15;
    }

    public Settings(String country, String city, boolean adan, boolean reminder, int period) {
        this.country = country;
        this.city = city;
        this.adan = adan;
        this.reminder = reminder;
        this.period = period;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", adan=" + adan +
                ", reminder=" + reminder +
                ", period=" + period +
                '}';
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getPeriod() {
        return period;
    }

    public String getCity() {
        return city;
    }

    public boolean isAdan() {
        return adan;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAdan(boolean adan) {
        this.adan = adan;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }

    public boolean isReminder() {
        return reminder;
    }

}
