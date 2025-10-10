package com.elevate5.elevateyou.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventManager {

    private final Map<LocalDate, ArrayList<Event>> events;

    public EventManager() {
        this.events = new HashMap<>();
    }

    public void addEvent(LocalDate date, Event event) {
        if(this.events.containsKey(date)) {
            this.events.get(date).add(event);
        }else{
            ArrayList<Event> list = new ArrayList<>();
            list.add(event);
            events.put(date, list);
        }
        //events.get(date).add(event);
    }

    public Map<LocalDate, ArrayList<Event>> getEvents() {
        return events;
    }

}
