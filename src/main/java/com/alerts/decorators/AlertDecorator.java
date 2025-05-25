package com.alerts.decorators;

import com.alerts.AlertInterface;

public class AlertDecorator implements AlertInterface {
    protected final AlertInterface decoratedAlert;
    public AlertDecorator(AlertInterface decoratedAlert) {this.decoratedAlert = decoratedAlert;}
    @Override
    public String getPatientId() {
        return decoratedAlert.getPatientId();
    }

    @Override
    public String getCondition() {
        return decoratedAlert.getCondition();
    }

    @Override
    public long getTimestamp() {
        return decoratedAlert.getTimestamp();
    }

    @Override
    public String getDetails() {
        return decoratedAlert.getDetails();
    }
}
