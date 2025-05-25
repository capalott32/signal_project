package com.alerts.Singleton;

import com.data_management.DataStorage;

/**
 * Provides a globally shared instance of DataStorage
 * Ensures consistent state across all components that access patient data
 */
public class SingletonDataStorage {
    private static SingletonDataStorage instance;
    private DataStorage dataStorage;

    private SingletonDataStorage() {
        dataStorage = new DataStorage();
    }

    public static synchronized SingletonDataStorage getInstance() {
        if (instance == null) {
            instance = new SingletonDataStorage();
        }
        return instance;
    }

    public DataStorage getDataStorage() {
        return dataStorage;
    }
}