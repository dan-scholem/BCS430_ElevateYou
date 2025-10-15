package com.elevate5.elevateyou.model;

import com.elevate5.elevateyou.model.calendardata.DayData;
import com.elevate5.elevateyou.model.calendardata.WeekData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CalendarModel {

    private ArrayList<WeekData> weeks;

    private Map<LocalDate,ArrayList<DayData>> eventMap;

    private static final Map<String, Integer> daysInMonth = new HashMap<String, Integer>();


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

        weeks = new ArrayList<WeekData>();

    }

    public static Map<String, Integer> getDaysInMonth() {
        return daysInMonth;
    }

    public void addWeek(WeekData week){
        weeks.add(week);
    }

    public ArrayList<WeekData> getWeeks() {
        return weeks;
    }

    public Map<LocalDate, ArrayList<DayData>> getDays() {
        return eventMap;
    }

}
