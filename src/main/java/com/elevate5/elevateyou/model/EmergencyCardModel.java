package com.elevate5.elevateyou.model;

import java.util.HashMap;
import java.util.Map;

public class EmergencyCardModel {

    private String fullName;
    private String phone;
    private String dob;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String conditions;
    private String allergies;
    private String medications;
    private String surgeryHistory;
    private String implants;
    private String anticoagulant;
    private String insurance;

    public EmergencyCardModel() {}

    public EmergencyCardModel(String fullName, String phone, String dob,
                              String emergencyContactName, String emergencyContactPhone,
                              String conditions, String allergies,
                              String medications, String surgeryHistory,
                              String implants, String anticoagulant, String insurance) {
        this.fullName = fullName;
        this.phone = phone;
        this.dob = dob;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.conditions = conditions;
        this.allergies = allergies;
        this.medications = medications;
        this.surgeryHistory = surgeryHistory;
        this.implants = implants;
        this.anticoagulant = anticoagulant;
        this.insurance = insurance;
    }

    public static EmergencyCardModel fromMap(Map<String, Object> m) {
        if (m == null) return null;
        return new EmergencyCardModel(
                (String) m.getOrDefault("fullName", ""),
                (String) m.getOrDefault("phone", ""),
                (String) m.getOrDefault("dob", ""),
                (String) m.getOrDefault("emergencyContactName", ""),
                (String) m.getOrDefault("emergencyContactPhone", ""),
                (String) m.getOrDefault("conditions", ""),
                (String) m.getOrDefault("allergies", ""),
                (String) m.getOrDefault("medications", ""),
                (String) m.getOrDefault("surgeryHistory", ""),
                (String) m.getOrDefault("implants", ""),
                (String) m.getOrDefault("anticoagulant", ""),
                (String) m.getOrDefault("insurance", "")
        );
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>();
        m.put("fullName", fullName);
        m.put("phone", phone);
        m.put("dob", dob);
        m.put("emergencyContactName", emergencyContactName);
        m.put("emergencyContactPhone", emergencyContactPhone);
        m.put("conditions", conditions);
        m.put("allergies", allergies);
        m.put("medications", medications);
        m.put("surgeryHistory", surgeryHistory);
        m.put("implants", implants);
        m.put("anticoagulant", anticoagulant);
        m.put("insurance", insurance);
        return m;
    }


    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getMedications() { return medications; }
    public void setMedications(String medications) { this.medications = medications; }

    public String getSurgeryHistory() { return surgeryHistory; }
    public void setSurgeryHistory(String surgeryHistory) { this.surgeryHistory = surgeryHistory; }

    public String getImplants() { return implants; }
    public void setImplants(String implants) { this.implants = implants; }

    public String getAnticoagulant() { return anticoagulant; }
    public void setAnticoagulant(String anticoagulant) { this.anticoagulant = anticoagulant; }

    public String getInsurance() { return insurance; }
    public void setInsurance(String insurance) { this.insurance = insurance; }
}