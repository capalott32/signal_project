package com.alerts.StrategyPatter;

import com.alerts.Alert;
import com.alerts.AlertListener;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BloodPressureStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, AlertListener listener) {
        List<PatientRecord> systolic = filterAndSort(patient, "SystolicPressure");
        List<PatientRecord> diastolic = filterAndSort(patient, "DiastolicPressure");

        checkTrends(systolic, patient.getPatientId(), "SystolicPressure", listener);
        checkTrends(diastolic, patient.getPatientId(), "DiastolicPressure", listener);

        checkThresholds(systolic, patient.getPatientId(), "SystolicPressure", 90, 180, listener);
        checkThresholds(diastolic, patient.getPatientId(), "DiastolicPressure", 60, 120, listener);
    }

    private List<PatientRecord> filterAndSort(Patient patient, String type) {
        return patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE).stream()
                .filter(r -> r.getRecordType().equalsIgnoreCase(type))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .collect(Collectors.toList());
    }

    private void checkThresholds(List<PatientRecord> records, int id, String type, double low, double high, AlertListener listener) {
        for (PatientRecord r : records) {
            if (r.getMeasurementValue() > high) {
                listener.onAlert(new Alert(String.valueOf(id), "High" + type + "Alert", r.getTimestamp()));
            } else if (r.getMeasurementValue() < low) {
                listener.onAlert(new Alert(String.valueOf(id), "Low" + type + "Alert", r.getTimestamp()));
            }
        }
    }

    private void checkTrends(List<PatientRecord> records, int id, String type, AlertListener listener) {
        for (int i = 0; i <= records.size() - 3; i++) {
            double v1 = records.get(i).getMeasurementValue();
            double v2 = records.get(i + 1).getMeasurementValue();
            double v3 = records.get(i + 2).getMeasurementValue();
            if ((v2 - v1 > 10 && v3 - v2 > 10) || (v1 - v2 > 10 && v2 - v3 > 10)) {
                listener.onAlert(new Alert(String.valueOf(id), type + "TrendAlert", records.get(i + 2).getTimestamp()));
            }
        }
    }
}