package com.alerts.Singleton;

import com.cardio_generator.HealthDataSimulator;

public class SingletonHealthDataSimulator {
    private static SingletonHealthDataSimulator instance;
    private HealthDataSimulator simulator;

    private SingletonHealthDataSimulator() {
        simulator = new HealthDataSimulator();
    }

    public static synchronized SingletonHealthDataSimulator getInstance() {
        if (instance == null) {
            instance = new SingletonHealthDataSimulator();
        }
        return instance;
    }

    public HealthDataSimulator getSimulator() {
        return simulator;
    }
}