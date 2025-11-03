package com.elevate5.elevateyou.viewmodel;

import com.elevate5.elevateyou.model.DoctorModel;
import com.elevate5.elevateyou.model.DoctorSearchModel;
import com.elevate5.elevateyou.model.LocationEntryModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class DoctorSearchViewModel {

    private final StringProperty firstName =  new SimpleStringProperty();
    private final StringProperty lastName  =  new SimpleStringProperty();
    private final StringProperty specialty =  new SimpleStringProperty();
    private final ObjectProperty<LocationEntryModel> location = new SimpleObjectProperty<>();

    public ObservableList<DoctorModel> searchDoctors(){
        String firstName = getFirstName();
        String lastName = getLastName();
        LocationEntryModel location = getLocation();
        //System.out.println(location.toString());
        String specialty = getSpecialty();
        return DoctorSearchModel.search(firstName,lastName,location,specialty);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public String getSpecialty() {
        return specialty.get();
    }

    public StringProperty specialtyProperty() {
        return specialty;
    }

    public LocationEntryModel getLocation() {
        return location.get();
    }

    public ObjectProperty<LocationEntryModel> locationProperty() {
        return location;
    }
}
