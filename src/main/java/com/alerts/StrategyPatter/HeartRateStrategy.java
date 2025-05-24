package com.alerts.StrategyPatter;

import com.alerts.Alert;
import com.alerts.AlertListener;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.Comparator;
import java.util.List;

public class HeartRateStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, AlertListener listener) {
        List<PatientRecord> heartRateRecords = patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE)
                .stream()
                .filter(r -> r.getRecordType().equalsIgnoreCase("HeartRate"))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .toList();

        for (PatientRecord r : heartRateRecords) {
            double value = r.getMeasurementValue();
            if (value < 60 || value > 100) {
                listener.onAlert(new Alert(String.valueOf(patient.getPatientId()), "AbnormalHeartRate", r.getTimestamp()));
            }
        }
    }
}
