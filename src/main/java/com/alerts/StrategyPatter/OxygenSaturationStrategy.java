package com.alerts.StrategyPatter;
import com.alerts.Alert;
import com.alerts.AlertListener;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OxygenSaturationStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, AlertListener listener) {
        List<PatientRecord> records = patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE).stream()
                .filter(r -> r.getRecordType().equalsIgnoreCase("BloodSaturation"))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .collect(Collectors.toList());

        for (PatientRecord r : records) {
            if (r.getMeasurementValue() < 92.0) {
                listener.onAlert(new Alert(String.valueOf(patient.getPatientId()), "LowSaturationAlert", r.getTimestamp()));
            }
        }

        for (int i = 0; i < records.size(); i++) {
            double start = records.get(i).getMeasurementValue();
            long startTime = records.get(i).getTimestamp();

            for (int j = i + 1; j < records.size(); j++) {
                double next = records.get(j).getMeasurementValue();
                long nextTime = records.get(j).getTimestamp();
                if (nextTime - startTime <= 10 * 60 * 1000) {
                    if (start - next >= 5.0) {
                        listener.onAlert(new Alert(String.valueOf(patient.getPatientId()), "RapidDropSaturationAlert", nextTime));
                        break;
                    }
                } else break;
            }
        }
    }
}