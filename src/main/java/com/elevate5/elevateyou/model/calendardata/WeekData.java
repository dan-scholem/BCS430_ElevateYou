package com.elevate5.elevateyou.model.calendardata;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.util.ArrayList;

public class WeekData {

    private ArrayList<DayData> dayData =  new ArrayList<>();

    private final ObjectProperty<DayData> sun = new SimpleObjectProperty<>();
    private final ObjectProperty<DayData> mon = new SimpleObjectProperty<>();
    private final ObjectProperty<DayData> tue = new SimpleObjectProperty<>();
    private final ObjectProperty<DayData> wed = new SimpleObjectProperty<>();
    private final ObjectProperty<DayData> thu = new SimpleObjectProperty<>();
    private final ObjectProperty<DayData> fri = new SimpleObjectProperty<>();
    private final ObjectProperty<DayData> sat = new SimpleObjectProperty<>();

    public void setDay(int index, DayData data){
        switch(index){
            case 0 -> sun.set(data);
            case 1 -> mon.set(data);
            case 2 -> tue.set(data);
            case 3 -> wed.set(data);
            case 4 -> thu.set(data);
            case 5 -> fri.set(data);
            case 6 -> sat.set(data);
        }
    }

    public void addDay(DayData data){
        dayData.add(data);
    }

    public DayData getSun() {
        return sun.get();
    }

    public ObjectProperty<DayData> sunProperty() {
        return sun;
    }

    public DayData getMon() {
        return mon.get();
    }

    public ObjectProperty<DayData> monProperty() {
        return mon;
    }

    public DayData getTue() {
        return tue.get();
    }

    public ObjectProperty<DayData> tueProperty() {
        return tue;
    }

    public DayData getWed() {
        return wed.get();
    }

    public ObjectProperty<DayData> wedProperty() {
        return wed;
    }

    public DayData getThu() {
        return thu.get();
    }

    public ObjectProperty<DayData> thuProperty() {
        return thu;
    }

    public DayData getFri() {
        return fri.get();
    }

    public ObjectProperty<DayData> friProperty() {
        return fri;
    }

    public DayData getSat() {
        return sat.get();
    }

    public ObjectProperty<DayData> satProperty() {
        return sat;
    }

}
