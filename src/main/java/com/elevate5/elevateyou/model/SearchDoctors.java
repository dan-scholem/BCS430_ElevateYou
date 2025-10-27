package com.elevate5.elevateyou.model;

import com.google.gson.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class SearchDoctors {

    private static final String apiURL = "https://npiregistry.cms.hhs.gov/api/?version=2.1";
    //private String searchStr;

    public static ObservableList<String> search(String firstName, String lastName, String location, String taxonomy_desc){
        try{
            String searchString = apiURL + "&first_name=" + firstName + "&last_name=" + lastName + "&city=" + location + "&taxonomy_description=" + taxonomy_desc;
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

                String jsonString = sb.toString();

                ObjectMapper mapper = new ObjectMapper();

                JsonNode root = mapper.readTree(jsonString);

                ArrayNode results = (ArrayNode) root.path("results");
                ObservableList<String> resultsList = FXCollections.observableArrayList();
                String pattern = "^\"|\"$";
                for(JsonNode node: results) {

                    String firstNameMatch = node.path("basic").path("first_name").toString().replaceAll(pattern, "");
                    String lastNameMatch = node.path("basic").path("last_name").toString().replaceAll(pattern, "");
                    String desc = node.path("taxonomies").get(0).get("desc").toString().replaceAll(pattern, "");

                    String address = node.path("addresses").get(1).get("address_1").toString().replaceAll(pattern, "");

                    String city = node.path("addresses").get(1).get("city").toString().replaceAll(pattern, "");

                    String state = node.path("addresses").get(1).get("state").toString().replaceAll(pattern, "");

                    String zip = node.path("addresses").get(1).get("postal_code").toString().replaceAll(pattern, "").substring(0, 5);

                    String telephone;
                    if(node.path("addresses").get(1).get("telephone_number") == null){
                        telephone = "";
                    }else {
                        telephone = node.path("addresses").get(1).get("telephone_number").toString().replaceAll(pattern, "");
                    }
                    String result = firstNameMatch + " " + lastNameMatch + ";" + address + " " + city + ", " + state + " " + zip + ";" + telephone + ";" + desc;

                    resultsList.add(result);

                }
                conn.disconnect();
                return resultsList;

            }else{
                System.out.println("Error");
                conn.disconnect();
                return null;
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){

        ObservableList<String> results = search("", "Lefkowitz", "", "cardio");
        for(String result: results){
            System.out.println(result);
        }
    }

}
