package com.alerts.factory;

import com.alerts.Alert;

public class ECGAlertFactory extends AlertFactory{
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {return new Alert(patientId, "ECG Alert: " + condition, timestamp);}
}
