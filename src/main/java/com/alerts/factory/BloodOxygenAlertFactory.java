package com.alerts.factory;

import com.alerts.Alert;

public class BloodOxygenAlertFactory extends AlertFactory{
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {return new Alert(patientId, "Blood Oxygen Alert: " + condition, timestamp);}
}
