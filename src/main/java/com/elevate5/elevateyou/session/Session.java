package com.elevate5.elevateyou.session;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.model.Event;
import com.elevate5.elevateyou.model.EventManager;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.UserRecord;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Session {

    private final UserRecord user;
    private final String userID;
    private EventManager userEventManager = new EventManager();

    public Session(UserRecord user) throws ExecutionException, InterruptedException {
        this.user = user;
        this.userID = user.getUid();
        //Load event data from Firestore db
        DocumentReference docRef = App.fstore.collection("Events").document(this.userID);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot doc = future.get();
        //check if document exists
        if(doc.exists()) {
            //deserialize Firestore data and load into user's event manager
            Map<String, Object> eventsMap = (Map<String, Object>) doc.get("events");
            for(String key : eventsMap.keySet()) {
                List<Map<String, Object>> eventsList = (List<Map<String, Object>>) eventsMap.get(key);
                for(Map<String, Object> data : eventsList){
                    String date = (String) data.get("date");
                    String time = (String) data.get("time");
                    String eventName = (String) data.get("eventName");
                    String eventDescription = (String) data.get("eventDescription");
                    Event event = new Event(date, time, eventName, eventDescription);
                    userEventManager.addEvent(date, event);
                }
            }

            System.out.println("Doc exists");
        }else{ //if document does not exist, create new document for firestore
            EventManager newEventData = new EventManager();
            try{
                ApiFuture<WriteResult> future1 = docRef.set(newEventData);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            userEventManager = doc.toObject(EventManager.class);
            System.out.println("Doc doesn't exist, document created");
        }

    }

    public UserRecord getUser() {
        return user;
    }

    public String getUserID() {
        return userID;
    }

    public EventManager getUserEventManager() {
        return userEventManager;
    }

    public void setUserEventManager(EventManager userEventManager) {
        this.userEventManager = userEventManager;
    }
}
