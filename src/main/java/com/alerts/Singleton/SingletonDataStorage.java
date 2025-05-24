package com.alerts.Singleton;

import com.data_management.DataStorage;

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