package com.alerts.StrategyPatter;
import com.data_management.Patient;

import com.alerts.Alert;
import com.alerts.AlertListener;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;
import java.util.List;



import static org.junit.jupiter.api.Assertions.*;

public class BloodPressureStrategyTest {

    @Test
    public void testBloodPressureThresholdAlertTriggered() {
        Patient patient = new Patient(1);
        BloodPressureStrategy strategy = new BloodPressureStrategy();

        // Add high and low systolic and diastolic values
        patient.addRecord(185, "SystolicPressure", System.currentTimeMillis());
        patient.addRecord(55, "DiastolicPressure", System.currentTimeMillis());


        strategy.checkAlert(patient, new AlertListener() {
            @Override
            public void onAlert(Alert alert) {
                assertTrue(alert.getCondition().contains("High") || alert.getCondition().contains("Low"));
            }
        });
    }

    @Test
    public void testBloodPressureTrendAlertTriggered() {
        Patient patient = new Patient(2);
        BloodPressureStrategy strategy = new BloodPressureStrategy();

        long now = System.currentTimeMillis();
        patient.addRecord(110, "SystolicPressure", now);
        patient.addRecord(125, "SystolicPressure", now + 1000);
        patient.addRecord(140, "SystolicPressure", now + 2000);

        strategy.checkAlert(patient, new AlertListener() {
            @Override
            public void onAlert(Alert alert) {
                assertTrue(alert.getCondition().contains("TrendAlert"));
            }
        });
    }
}
