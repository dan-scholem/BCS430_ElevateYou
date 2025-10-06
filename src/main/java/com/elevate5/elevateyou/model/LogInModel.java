package com.elevate5.elevateyou.model;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.UserLogin;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LogInModel {

    public UserRecord user;


    public boolean passwordAuth(String email, String password) {

        try{
            InputStream jsonIn = getClass().getResourceAsStream("/com/elevate5/elevateyou/FirebaseAPI.json");
            JsonObject config;
            String apiKey;
            try(InputStreamReader reader = new InputStreamReader(jsonIn, StandardCharsets.UTF_8)){
                config = new Gson().fromJson(reader, JsonObject.class);
                apiKey = config.get("api_key").getAsString();
            }

            String endpoint = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;

            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);

            String jsonInput = String.format("""
                    {
                     "email": "%s",
                     "password": "%s",
                     "returnSecureToken": true
                    }
                    """, email, password);

            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if(connection.getResponseCode() == 200){
                user = App.fauth.getUserByEmail(email);
                return true;
            } else{
                System.out.println(connection.getResponseMessage() + connection.getResponseCode());
                return false;
            }

        } catch(FileNotFoundException e){
            System.out.println("File not found: " + e.getMessage());
        } catch(IOException e){
            System.out.println("Error reading file: " + e.getMessage());
        } catch(FirebaseAuthException e){
            System.out.println("Firebase auth error: " + e.getMessage());
        }
        return false;

    }

}
