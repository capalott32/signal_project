package com.alerts.Singleton;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SingletonTest {

    @Test
    public void testDataStorageSingletonInstance() {
        SingletonDataStorage instance1 = SingletonDataStorage.getInstance();
        SingletonDataStorage instance2 = SingletonDataStorage.getInstance();

        assertSame(instance1, instance2, "SingletonDataStorage should return the same instance");
    }

    @Test
    public void testHealthDataSimulatorSingletonInstance() {
        SingletonHealthDataSimulator instance1 = SingletonHealthDataSimulator.getInstance();
        SingletonHealthDataSimulator instance2 = SingletonHealthDataSimulator.getInstance();

        assertSame(instance1, instance2, "SingletonHealthDataSimulator should return the same instance");
    }
}
