package com.elevate5.elevateyou.session;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.model.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.UserRecord;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Session {

    private final UserRecord user;
    private final String userID;
    private EventManager userEventManager = new EventManager();
    private AppointmentManager userAppointmentManager = new AppointmentManager();
    private DoctorModel selectedDoctor;

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

        //Load appointment data from Firestore db
        docRef = App.fstore.collection("Appointments").document(this.userID);
        future = docRef.get();
        doc = future.get();

        if(doc.exists()) {
            List<Map<String, Object>> appointmentsMap = (List<Map<String, Object>>) doc.get("appointments");
            if(appointmentsMap != null) {
                for(Map<String, Object> data : appointmentsMap) {
                    String date = (String) data.get("date");
                    String time = (String) data.get("time");
                    String docName = (String) data.get("docName");
                    String docPhone = (String) data.get("docPhone");
                    String docType = (String) data.get("type");
                    String notes =  (String) data.get("notes");
                    String address = (String) data.get("address");
                    AppointmentModel appointment = new AppointmentModel(date, time, docName, docType, docPhone, address, notes);
                    userAppointmentManager.addAppointment(appointment);
                }
            }

        }else{
            AppointmentManager newAppointmentData = new AppointmentManager();
            try{
                ApiFuture<WriteResult> future1 = docRef.set(newAppointmentData);
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
            userAppointmentManager = doc.toObject(AppointmentManager.class);

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

    public AppointmentManager getUserAppointmentManager() {
        return userAppointmentManager;
    }

    public DoctorModel getSelectedDoctor() {
        return selectedDoctor;
    }

    public void setSelectedDoctor(DoctorModel selectedDoctor) {
        this.selectedDoctor = selectedDoctor;
    }
}
