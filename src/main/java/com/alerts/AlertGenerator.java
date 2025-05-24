package com.alerts;


import com.data_management.DataStorage;
import com.data_management.Patient;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import com.data_management.PatientRecord;
import java.util.Comparator;
import org.junit.jupiter.api.Test;
import java.util.Deque;


/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator implements AlertListener{
    private DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> allRecords = patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE);

        List<PatientRecord> systolic = allRecords.stream()
                .filter(r -> r.getRecordType().equalsIgnoreCase("SystolicPressure"))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .collect(Collectors.toList());

        List<PatientRecord> diastolic = allRecords.stream()
                .filter(r -> r.getRecordType().equalsIgnoreCase("DiastolicPressure"))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .collect(Collectors.toList());

        checkTrends(systolic, patient.getPatientId(), "SystolicPressure");
        checkTrends(diastolic, patient.getPatientId(), "DiastolicPressure");

        checkThresholds(systolic, diastolic, patient.getPatientId());
        checkHypotensiveHypoxemiaAlert(patient);
        checkEcgAlerts(patient);
    }

    private void checkTrends(List<PatientRecord> records, int patientId, String type) {
        for (int i = 2; i < records.size(); i++) {
            double v1 = records.get(i - 2).getMeasurementValue();
            double v2 = records.get(i - 1).getMeasurementValue();
            double v3 = records.get(i).getMeasurementValue();

            long ts = records.get(i).getTimestamp();

            if ((v2 - v1 > 10 && v3 - v2 > 10)) {
                triggerAlert(new Alert(String.valueOf(patientId), type + " Increasing Trend", ts));
            } else if ((v1 - v2 > 10 && v2 - v3 > 10)) {
                triggerAlert(new Alert(String.valueOf(patientId), type + " Decreasing Trend", ts));
            }
        }
    }

    private void checkThresholds(List<PatientRecord> systolic, List<PatientRecord> diastolic, int patientId) {
        int size = Math.min(systolic.size(), diastolic.size());
        for (int i = 0; i < size; i++) {
            double sys = systolic.get(i).getMeasurementValue();
            double dia = diastolic.get(i).getMeasurementValue();
            long ts = Math.max(systolic.get(i).getTimestamp(), diastolic.get(i).getTimestamp());

            if (sys > 180 || sys < 90 || dia > 120 || dia < 60) {
                triggerAlert(new Alert(String.valueOf(patientId), "CriticalThresholdBreached", ts));
            }
        }
    }

    private void triggerAlert(Alert alert) {
        System.out.println("ALERT: Patient " + alert.getPatientId()
                + " | Condition: " + alert.getCondition()
                + " | Timestamp: " + alert.getTimestamp());
    }
    private void checkHypotensiveHypoxemiaAlert(Patient patient){
        List<PatientRecord> records = patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE);
        List<PatientRecord> bpRecords = records.stream().filter(r -> r.getRecordType().equalsIgnoreCase("Systolic Blood Pressure")).sorted(Comparator.comparingLong(PatientRecord::getTimestamp)).toList();
        List<PatientRecord> o2Records = records.stream().filter(r -> r.getRecordType().equalsIgnoreCase("Oxygen Saturation")).sorted(Comparator.comparingLong(PatientRecord::getTimestamp)).toList();
        for(PatientRecord bp : bpRecords) {
            if (bp.getMeasurementValue() >= 90) continue;
            for (PatientRecord o2 : o2Records) {
                if (Math.abs(bp.getTimestamp() - o2.getTimestamp()) <= 60000) {
                    if (o2.getMeasurementValue() < 92) {
                        triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "Hypotensive Hypoxemia Alert", bp.getTimestamp()));
                        return;
                    }
                }
            }
        }
    }
    private void checkEcgAlerts(Patient patient){
        List<PatientRecord> ecgRecords = patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE).stream().filter(r -> r.getRecordType().equalsIgnoreCase("ECG")).sorted(Comparator.comparingLong(PatientRecord::getTimestamp)).toList();
        Deque<Double> ecgWindow = new LinkedList<>();
        final int WINDOW_SIZE = 10;
        final double THRESHOLD_FACTOR = 2.0;
        for(PatientRecord record : ecgRecords){
            double ecgValue = record.getMeasurementValue();
            ecgWindow.addLast(ecgValue);
            if(ecgWindow.size() > WINDOW_SIZE) ecgWindow.pollFirst();
            double mean = ecgWindow.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double stdDev = Math.sqrt(ecgWindow.stream().mapToDouble(v -> Math.pow(v - mean, 2)).average().orElse(0.0));
            if(ecgWindow.size() == WINDOW_SIZE && ecgValue > mean + THRESHOLD_FACTOR*stdDev) triggerAlert(new Alert(String.valueOf(patient.getPatientId()), "ECG Anomaly Detected", record.getTimestamp()));
        }
    }

    @Override
    public void onAlert(Alert alert) {
        if("Triggered Alert".equalsIgnoreCase(alert.getCondition())) triggerAlert(alert);
    }
} /*Filters and sorts systolic and diastolic records.

Detects increasing/decreasing trends across 3 values.

Checks threshold breaches for both systolic (>180 or <90) and diastolic (>120 or <60).

Sends all matched cases to triggerAlert(...).*/