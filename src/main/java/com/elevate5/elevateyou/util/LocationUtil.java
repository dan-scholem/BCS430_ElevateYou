package com.elevate5.elevateyou.util;

import com.elevate5.elevateyou.model.LocationEntryModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class LocationUtil {

    public static boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2, double radius) {

        final double milesPerDegreeLat = 69.0;
        final double milesPerDegreeLong = 69.17 * Math.cos(Math.toRadians(lat1));

        double diffLatitude = (lat2 - lat1);
        double diffLongitude = (lon2 - lon1);

        double diffLatToMiles = diffLatitude * milesPerDegreeLat;
        double diffLonToMiles = diffLongitude * milesPerDegreeLong;

        double distance = Math.sqrt(Math.pow(diffLatToMiles, 2) + Math.pow(diffLonToMiles, 2));

        return distance <= radius;

    }

    public static ObservableList<LocationEntryModel> getLocationsWithinRadius(LocationEntryModel selectedLocation, double searchRadius, List<LocationEntryModel> locations){
        ObservableList<LocationEntryModel> matchedLocations = FXCollections.observableArrayList();
        double lat1 = selectedLocation.getLatitude();
        double lon1 = selectedLocation.getLongitude();
        for(LocationEntryModel location : locations){
            if(isWithinRadius(lat1, lon1, location.getLatitude(), location.getLongitude(), searchRadius) && !selectedLocation.getPostalCode().equals(location.getPostalCode())){
                System.out.println("Matched location: " + location);
                matchedLocations.add(location);
            }
        }
        return matchedLocations;
    }

}
