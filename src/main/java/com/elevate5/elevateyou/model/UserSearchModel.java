package com.elevate5.elevateyou.model;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.session.SessionManager;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import javax.swing.text.Document;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UserSearchModel {

    public UserSearchModel() {
    }

    public ArrayList<DocumentSnapshot> searchUsers(String searchString){
        System.out.println(searchString);
        ArrayList<DocumentSnapshot> userDocs = new ArrayList<>();

        ApiFuture<QuerySnapshot> future = App.fstore.collection("Users").get();
        List<QueryDocumentSnapshot> documents;

        try {
            documents = future.get().getDocuments();

            if (!documents.isEmpty()) {

                for (QueryDocumentSnapshot document : documents) {
                    String docFirstName = document.getString("FirstName");
                    String docLastName = document.getString("LastName");
                    assert docFirstName != null;
                    assert docLastName != null;
                    if (docFirstName.toLowerCase().contains(searchString) ||
                            docLastName.toLowerCase().contains(searchString)){
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
