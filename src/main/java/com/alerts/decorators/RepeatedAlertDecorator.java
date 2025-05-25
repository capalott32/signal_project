package com.alerts.decorators;

import com.alerts.AlertInterface;

public class RepeatedAlertDecorator extends AlertDecorator{
    private final int repeatCount;
    public RepeatedAlertDecorator(AlertInterface alert, int repeatCount) {
        super(alert);
        this.repeatCount = repeatCount;
    }
    @Override
    public String getDetails(){return decoratedAlert.getDetails() + "| Repeats: " + repeatCount;}
}
