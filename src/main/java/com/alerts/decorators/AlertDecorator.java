package com.alerts.decorators;

import com.alerts.AlertInterface;

//Base decorator class for extending Alert behavior without modifying the core Alert logic
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
