package com.elevate5.elevateyou.model;

import com.elevate5.elevateyou.App;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserSearchModel {

    private final DocumentReference currUserDocRef;

    public UserSearchModel(DocumentReference userDocRef) {
        this.currUserDocRef = userDocRef;
    }

    public ArrayList<DocumentSnapshot> searchUsers(String searchString){
        ArrayList<DocumentSnapshot> userDocs = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = App.fstore.collection("Users").get();
        List<QueryDocumentSnapshot> documents;

        try {
            documents = future.get().getDocuments();

            if (!documents.isEmpty()) {

                for (QueryDocumentSnapshot document : documents) {
                    String docFirstName = document.getString("FirstName");
                    String docLastName = document.getString("LastName");
                    String fullName = docFirstName + " " + docLastName;
                    String docId = document.getId();

                    ArrayList<String> blockedUsers;
                    if(document.get("BlockedUsers") != null){
                        blockedUsers = new ArrayList<>((List<String>)  document.get("BlockedUsers"));
                    }else{
                        blockedUsers = new ArrayList<>();
                    }
                    assert docFirstName != null;
                    assert docLastName != null;
                    if (!blockedUsers.contains(currUserDocRef.getId()) &&
                            !docId.equals(currUserDocRef.getId()) &&
                            (docFirstName.toLowerCase().contains(searchString) ||
                            docLastName.toLowerCase().contains(searchString) || fullName.toLowerCase().contains(searchString))) {
                        userDocs.add(document);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return userDocs;
    }

}
