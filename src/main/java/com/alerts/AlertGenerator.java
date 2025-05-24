package com.alerts;


import com.data_management.DataStorage;
import com.data_management.Patient;
import java.util.List;
import java.util.stream.Collectors;
import com.data_management.PatientRecord;
import java.util.Comparator;
import org.junit.jupiter.api.Test;


/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
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
    @Test
    void testBloodPressureAlerts() {
        DataStorage storage = new DataStorage();
        Patient patient = new Patient(1);

        // Simulate abnormal BP
        patient.addRecord(185, "SystolicPressure", System.currentTimeMillis());
        patient.addRecord(65, "DiastolicPressure", System.currentTimeMillis());

        // Simulate increasing trend
        patient.addRecord(110, "SystolicPressure", 1);
        patient.addRecord(125, "SystolicPressure", 2);
        patient.addRecord(140, "SystolicPressure", 3);

        com.alerts.AlertGenerator generator = new com.alerts.AlertGenerator(storage);
        generator.evaluateData(patient);

        // Check console output or captured alerts
    }

} /*Filters and sorts systolic and diastolic records.

Detects increasing/decreasing trends across 3 values.

Checks threshold breaches for both systolic (>180 or <90) and diastolic (>120 or <60).

Sends all matched cases to triggerAlert(...).*/