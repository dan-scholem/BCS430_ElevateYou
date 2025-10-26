package com.elevate5.elevateyou.model;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SearchDoctors {

    private static final String apiURL = "https://npiregistry.cms.hhs.gov/api/?version=2.1";
    //private String searchStr;

    public static void search(String firstName, String lastName, String location, String taxonomy_desc){
        try{
            String searchString = apiURL + "&first_name=" + firstName + "&last_name=" + lastName + "&city=" + location + "&taxonomy_descricption=" + taxonomy_desc;
            URL url = new URL(searchString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            System.out.println("Search response code: " + responseCode);

            if(responseCode == 200){
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();

                System.out.println(sb);
            }else{
                System.out.println("Error");
            }
            conn.disconnect();



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){

        search("Dean", "Lumley", "", "");
    }

}
