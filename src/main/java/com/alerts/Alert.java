package com.alerts;

// Represents an alert
public class Alert implements AlertInterface {
    private final String patientId;
    private final String condition;
    private final long timestamp;

    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getDetails() {return "ALERT: Patient " + patientId + " | Condition " + condition + " | Timestamp " + timestamp;}
}
