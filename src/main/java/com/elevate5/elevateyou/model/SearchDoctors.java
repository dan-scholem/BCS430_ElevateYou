package com.elevate5.elevateyou.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SearchDoctors {

    private final String docAPI = "https://npiregistry.cms.hhs.gov/api/?version=2.1";
    private String name;
    private String location;
    private String taxonomy;
    private String searchStr;

    public SearchDoctors(String name, String location, String taxonomy) {
        this.name = name;
        this.location = location;
        this.taxonomy = taxonomy;
    }

    public ArrayList<ArrayList<String>> buildQuery(String name, String zip, String taxonomy) throws IOException {
        searchStr = docAPI + "&first_name" + name + "&postal_code" + zip + "&taxonomy_description" + taxonomy;
        URL url = new URL(searchStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        System.out.println("Response code: " + conn.getResponseCode());
        InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
        JsonObject response = new Gson().fromJson(inputStreamReader, JsonObject.class);
        JsonArray results = response.getAsJsonArray("results");
        ArrayList<ArrayList<String>> doctors = new ArrayList<>();
        for(JsonElement result : results){
            ArrayList<String> docInfo = new ArrayList<>();
            JsonObject doc = result.getAsJsonObject();
            JsonObject basicInfo = doc.getAsJsonObject("basic");
            String docName = basicInfo.get("first_name").getAsString() + " " + basicInfo.get("last_name").getAsString();

            JsonArray taxonomies = doc.getAsJsonArray("taxonomies");
            String specialty = taxonomies.get(0).getAsJsonObject().get("desc").getAsString();
            docInfo.add(docName);
            docInfo.add(specialty);
            doctors.add(docInfo);
        }
        return doctors;
    }

}
