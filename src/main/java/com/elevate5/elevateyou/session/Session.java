package com.elevate5.elevateyou.session;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.model.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.UserRecord;
import javafx.scene.web.WebView;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Session {

    private final UserRecord user;
    private final String userID;
    private EventManager userEventManager = new EventManager();
    private AppointmentManager userAppointmentManager = new AppointmentManager();
    private SavedArticlesManager savedArticlesManager = new SavedArticlesManager();
    private DoctorModel selectedDoctor;
    private User currUser;
    private DocumentReference savedArticlesDocRef;
    private WebView webView;
    private int calorieGoal;
    private final DocumentReference caloriesDocRef;
    private final Map<String, Object> weightEntryMap;
    private final DocumentReference weightLogDocRef;

    public Session(UserRecord user) throws ExecutionException, InterruptedException {
        webView = new WebView();
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
        }else{ //if document does not exist, create new document for firestore
            EventManager newEventData = new EventManager();
            try{
                ApiFuture<WriteResult> future1 = docRef.set(newEventData);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            //userEventManager = doc.toObject(EventManager.class);
            userEventManager =  new EventManager();
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
            //userAppointmentManager = doc.toObject(AppointmentManager.class);
            userAppointmentManager = new AppointmentManager();

        }

        docRef = App.fstore.collection("Users").document(this.userID);
        future = docRef.get();
        doc = future.get();

        if(doc.exists()) {
            String firstName = (String) doc.get("FirstName");
            String lastName = (String) doc.get("LastName");
            String email = (String) doc.get("Email");
            String profilePicUrl = (String) doc.get("ProfilePicUrl");
            String bio = (String) doc.get("UserBio");
            String weight = (String) doc.get("UserWeight");
            this.currUser = new User(firstName, lastName, email, profilePicUrl, userID, bio);
            try{
                assert weight != null;
                this.currUser.setWeight(Integer.parseInt(weight));
            } catch (NumberFormatException e) {
                this.currUser.setWeight(0);
            }
            ArrayList<String> friendsList;
            if(doc.get("Friends") != null) {
                friendsList = new ArrayList<>((List<String>) doc.get("Friends"));
            }else{
                friendsList = new ArrayList<>();
            }
            this.currUser.setFriendsList(friendsList);
            ArrayList<String> receivedFriendRequestsList;
            if(doc.get("ReceivedFriendRequests") != null) {
                receivedFriendRequestsList = new ArrayList<>((List<String>) doc.get("ReceivedFriendRequests"));
            }else{
                receivedFriendRequestsList = new ArrayList<>();
            }
            this.currUser.setReceivedFriendRequestsList(receivedFriendRequestsList);
            ArrayList<String> sentFriendRequestsList;
            if(doc.get("SentFriendRequests") != null) {
                sentFriendRequestsList = new ArrayList<>((List<String>) doc.get("SentFriendRequests"));
            }else{
                sentFriendRequestsList = new ArrayList<>();
            }
            this.currUser.setSentFriendRequestsList(sentFriendRequestsList);
            ArrayList<String> blockList;
            if(doc.get("BlockedUsers") != null) {
                blockList = new ArrayList<>((List<String>) doc.get("BlockedUsers"));
            }else {
                blockList = new ArrayList<>();
            }
            this.currUser.setBlockList(blockList);
            if(doc.get("UserBio") == null){
                this.currUser.setUserBio("");
            }
        }else{
            System.out.println("User doesn't exist");
        }
        savedArticlesDocRef = App.fstore.collection("SavedArticles").document(this.userID);
        future = savedArticlesDocRef.get();
        doc = future.get();
        if(doc.exists()) {
            List<Map<String, Object>>  savedArticlesMap = (List<Map<String, Object>>) doc.get("savedArticles");
            if(savedArticlesMap != null) {
                for(Map<String, Object> data : savedArticlesMap) {
                    String articleTitle = (String) data.get("title");
                    String articleDescription = (String) data.get("description");
                    String articleAuthor = (String) data.get("author");
                    String articleUrl = (String)  data.get("articleUrl");
                    String articleImageUrl = (String)  data.get("articleImageUrl");
                    ArticleModel article  = new ArticleModel(articleUrl, articleAuthor, articleTitle, articleDescription, articleImageUrl);
                    savedArticlesManager.addArticle(article);
                }
            }
        }else{
            try{
                ApiFuture<WriteResult> future1 = savedArticlesDocRef.set(savedArticlesManager);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        caloriesDocRef = App.fstore.collection("Calories").document(this.userID);
        future = caloriesDocRef.get();
        doc = future.get();
        if(doc.exists()) {
            int calorieGoal = Integer.parseInt(doc.getData().get("CalorieGoal").toString());
            setCalorieGoal(calorieGoal);
        }

        weightEntryMap = new HashMap<>();
        weightLogDocRef = App.fstore.collection("Weight Log").document(this.userID);
        future = weightLogDocRef.get();
        doc = future.get();
        if(doc.exists()) {
            Map<String, Object> weightLogMap = doc.getData();
            if(weightLogMap != null) {
                weightEntryMap.putAll(weightLogMap);
            }
        }

    }

    public void saveCalorieGoalToFirestore(){
        Map<String,Object> calorieGoalMap = new HashMap<>();
        calorieGoalMap.put("CalorieGoal",this.calorieGoal);
        ApiFuture<WriteResult> result = this.caloriesDocRef.update(calorieGoalMap);
    }

    public void saveWeightEntryToSession(int weight){
        weightEntryMap.put(String.valueOf(LocalDate.now()), weight);
    }

    public void saveWeightLogToFireStore(){
        ApiFuture<WriteResult> result = this.weightLogDocRef.set(weightEntryMap);
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

    public User getCurrUser() {
        return currUser;
    }

    public void setCurrUser(User currUser) {
        this.currUser = currUser;
    }

    public SavedArticlesManager getSavedArticlesManager() {
        return savedArticlesManager;
    }

    public void setSavedArticlesManager(SavedArticlesManager savedArticlesManager) {
        this.savedArticlesManager = savedArticlesManager;
    }

    public DocumentReference getSavedArticlesDocRef() {
        return savedArticlesDocRef;
    }

    public void setSavedArticlesDocRef(DocumentReference savedArticlesDocRef) {
        this.savedArticlesDocRef = savedArticlesDocRef;
    }

    public WebView getWebView() {
        return webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    public int getCalorieGoal() {
        return calorieGoal;
    }

    public void setCalorieGoal(int calorieGoal) {
        this.calorieGoal = calorieGoal;
    }

    public DocumentReference getCaloriesDocRef() {
        return caloriesDocRef;
    }

    public Map<String, Object> getWeightEntryMap() {
        return weightEntryMap;
    }
}
