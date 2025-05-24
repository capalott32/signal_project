package com.alerts.StrategyPatter;

import com.alerts.Alert;
import com.alerts.AlertListener;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OxygenSaturationStrategyTest {

    @Test
    public void testLowSaturationAlert() {
        Patient patient = new Patient(3);
        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy();

        // Add low oxygen value to trigger alert (< 92)
        patient.addRecord(89, "Oxygen Saturation", System.currentTimeMillis());

        strategy.checkAlert(patient, new AlertListener() {
            @Override
            public void onAlert(Alert alert) {
                assertEquals("LowOxygenSaturationAlert", alert.getCondition());
            }
        });
    }

    @Test
    public void testRapidDropAlert() {
        Patient patient = new Patient(4);
        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy();

        long now = System.currentTimeMillis();

        // First reading (high)
        patient.addRecord(95, "Oxygen Saturation", now);
        // Drop of more than 5% within 10 minutes
        patient.addRecord(89, "Oxygen Saturation", now + 5 * 60 * 1000); // 5 minutes later

        strategy.checkAlert(patient, new AlertListener() {
            @Override
            public void onAlert(Alert alert) {
                assertEquals("RapidOxygenDropAlert", alert.getCondition());
            }
        });
    }
}