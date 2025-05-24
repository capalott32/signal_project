package com.alerts.StrategyPatter;
import com.alerts.Alert;

import com.alerts.AlertListener;
import com.data_management.Patient;

public interface AlertStrategy {
    void checkAlert(Patient patient, AlertListener listener);
}