package com.elevate5.elevateyou.model;

import com.google.cloud.Timestamp;
import java.util.Map;

public class SleepModel {
    private String startIso;
    private String endIso;
    private Integer durationMin;
    private Integer nap;
    private Integer totalMin;
    private Map<String, Object> factors;

    public SleepModel() {
    }

    public SleepModel(String startIso, String endIso, Integer durationMin, Integer nap, Integer totalMin, Map<String, Object> factors) {
        this.startIso = startIso;
        this.endIso = endIso;
        this.durationMin = durationMin;
        this.nap = nap;
        this.totalMin = totalMin;
        this.factors = factors;
    }

    public String getStartIso() {
        return startIso;
    }

    public void setStartIso(String startIso) {
        this.startIso = startIso;
    }

    public String getEndIso() {
        return endIso;
    }

    public void setEndIso(String endIso) {
        this.endIso = endIso;
    }

    public Integer getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(Integer durationMin) {
        this.durationMin = durationMin;
    }

    public Integer getNap() {
        return nap;
    }

    public void setNap(Integer nap) {
        this.nap = nap;
    }

    public Integer getTotalMin() {
        return totalMin;
    }

    public void setTotalMin(Integer totalMin) {
        this.totalMin = totalMin;
    }

    public Map<String, Object> getFactors() {
        return factors;
    }

    public void setFactors(Map<String, Object> factors) {
        this.factors = factors;
    }

    public boolean isComplete() {
        return startIso != null && !startIso.isBlank()
                && endIso != null && !endIso.isBlank()
                && durationMin != null
                && totalMin != null;
    }
}