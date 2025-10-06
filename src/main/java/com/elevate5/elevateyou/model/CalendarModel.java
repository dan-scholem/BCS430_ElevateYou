package com.elevate5.elevateyou.model;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CalendarModel {

    private final ArrayList<String> months = new ArrayList<>(Arrays.asList("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"));

    private final Map<String, Integer> daysInMonth = new HashMap<String, Integer>();

    private final SimpleStringProperty sun = new SimpleStringProperty();
    private final SimpleStringProperty mon = new SimpleStringProperty();
    private final SimpleStringProperty tue = new SimpleStringProperty();
    private final SimpleStringProperty wed = new SimpleStringProperty();
    private final SimpleStringProperty thu = new SimpleStringProperty();
    private final SimpleStringProperty fri = new SimpleStringProperty();
    private final SimpleStringProperty sat = new SimpleStringProperty();

    public CalendarModel() {
        daysInMonth.put("JANUARY", 31);
        daysInMonth.put("FEBRUARY", 28);
        daysInMonth.put("MARCH", 31);
        daysInMonth.put("APRIL", 30);
        daysInMonth.put("MAY", 31);
        daysInMonth.put("JUNE", 30);
        daysInMonth.put("JULY", 31);
        daysInMonth.put("AUGUST", 31);
        daysInMonth.put("SEPTEMBER", 30);
        daysInMonth.put("OCTOBER", 31);
        daysInMonth.put("NOVEMBER", 30);
        daysInMonth.put("DECEMBER", 31);
    }

    public Map<String, Integer> getDaysInMonth() {
        return daysInMonth;
    }

    public void setDay(int index, String value){
        switch(index){
            case 0 -> sun.set(value);
            case 1 -> mon.set(value);
            case 2 -> tue.set(value);
            case 3 -> wed.set(value);
            case 4 -> thu.set(value);
            case 5 -> fri.set(value);
            case 6 -> sat.set(value);
        }
    }

    public String getSun() {
        return sun.get();
    }

    public SimpleStringProperty sunProperty() {
        return sun;
    }

    public String getMon() {
        return mon.get();
    }

    public SimpleStringProperty monProperty() {
        return mon;
    }

    public String getTue() {
        return tue.get();
    }

    public SimpleStringProperty tueProperty() {
        return tue;
    }

    public String getWed() {
        return wed.get();
    }

    public SimpleStringProperty wedProperty() {
        return wed;
    }

    public String getThu() {
        return thu.get();
    }

    public SimpleStringProperty thuProperty() {
        return thu;
    }

    public String getFri() {
        return fri.get();
    }

    public SimpleStringProperty friProperty() {
        return fri;
    }

    public String getSat() {
        return sat.get();
    }

    public SimpleStringProperty satProperty() {
        return sat;
    }



}
