package com.elevate5.elevateyou.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventManager {

    private Map<String, ArrayList<Event>> events;

    public EventManager() {
        this.events = new HashMap<String, ArrayList<Event>>();
    }

    public EventManager(Map<String, ArrayList<Event>> events) {
        this.events = events;
    }

    public void addEvent(String date, Event event) {
        if(this.events != null && this.events.containsKey(date)) {
            this.events.get(date).add(event);
        }else{
            ArrayList<Event> list = new ArrayList<>();
            list.add(event);
            if(this.events == null){
                this.events = new HashMap<String, ArrayList<Event>>();
            }
            events.put(date, list);
        }
        //events.get(date).add(event);
    }

    public Map<String, ArrayList<Event>> getEvents() {
        return events;
    }

    public void setEvents(Map<String, ArrayList<Event>> events) {
        this.events = events;
    }

}
