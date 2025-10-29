package com.elevate5.elevateyou.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DoctorSearchModel {

    private static final String apiURL = "https://npiregistry.cms.hhs.gov/api/?version=2.1";

    public static ObservableList<DoctorModel> search(String firstName, String lastName, LocationEntryModel location, String taxonomy_desc){
        try{
            if(firstName == null){
                firstName = "";
            }
            if(lastName == null){
                lastName = "";
            }
            String postalCode;
            if(location == null){
                postalCode = "";
            }else{
                postalCode = location.getPostalCode();
            }
            if(taxonomy_desc == null){
                taxonomy_desc = "";
            }
            String searchString = apiURL + "&first_name=" + firstName + "&last_name=" + lastName + "&postal_code=" + postalCode + "&taxonomy_description=" + taxonomy_desc + "&limit=50";
            URL url = new URL(searchString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //System.out.println(searchString);

            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            //System.out.println("Search response code: " + responseCode);

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
                ObservableList<DoctorModel> doctors = FXCollections.observableArrayList();
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
                    DoctorModel doctorModel = new DoctorModel(firstNameMatch, lastNameMatch, desc, telephone, address + " " + city + ", " + state + " " + zip);
                    doctors.add(doctorModel);

                }
                conn.disconnect();
                return doctors;

            }else{
                System.out.println("Error");
                conn.disconnect();
                return null;
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch(ClassCastException e){
            System.out.println(e.getMessage());
            throw e;
        }
    }

}
