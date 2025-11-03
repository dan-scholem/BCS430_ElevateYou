package com.elevate5.elevateyou.util;

import com.elevate5.elevateyou.model.LocationEntryModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LocationUtil {

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {

        final double milesPerDegreeLat = 69.0;
        final double milesPerDegreeLong = 69.17 * Math.cos(Math.toRadians(lat1));

        double diffLatitude = (lat2 - lat1);
        double diffLongitude = (lon2 - lon1);

        double diffLatToMiles = diffLatitude * milesPerDegreeLat;
        double diffLonToMiles = diffLongitude * milesPerDegreeLong;

        return Math.sqrt(Math.pow(diffLatToMiles, 2) + Math.pow(diffLonToMiles, 2));

    }

    public static ObservableList<LocationEntryModel> getLocationsWithinRadius(LocationEntryModel selectedLocation, double searchRadius, List<LocationEntryModel> locations){
        ObservableList<LocationEntryModel> matchedLocations = FXCollections.observableArrayList();
        Map<LocationEntryModel, Double> distanceMap = new HashMap<>();
        double lat1 = selectedLocation.getLatitude();
        double lon1 = selectedLocation.getLongitude();
        double distance;
        for(LocationEntryModel location : locations){
            distance = calculateDistance(lat1, lon1, location.getLatitude(), location.getLongitude());
            if((distance <= searchRadius) && (!selectedLocation.getPostalCode().equals(location.getPostalCode()))){
                //System.out.println("Matched location: " + location);
                matchedLocations.add(location);
                distanceMap.put(location, distance);
            }
        }
        FXCollections.sort(matchedLocations, Comparator.comparingDouble(distanceMap::get));
        return matchedLocations;
    }

}
