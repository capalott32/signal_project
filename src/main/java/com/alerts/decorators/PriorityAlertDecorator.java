package com.alerts.decorators;

import com.alerts.AlertInterface;

public class PriorityAlertDecorator extends AlertDecorator{
    private final String priorityLevel;

    public PriorityAlertDecorator(AlertInterface alert, String priorityLevel) {
        super(alert);
        this.priorityLevel = priorityLevel;
    }
    @Override
    public String getDetails(){return "[PRIORITY: " + priorityLevel + "] " + decoratedAlert.getDetails();}
}
