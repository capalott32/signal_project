package com.data_management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileDataReader implements DataReader {
    private final DataStorage storage;

    public FileDataReader(DataStorage storage) {
        this.storage = storage;
    }

    @Override
    public void readData(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Format: patientId,timestamp,label,value
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int patientId = Integer.parseInt(parts[0]);
                    long timestamp = Long.parseLong(parts[1]);
                    String label = parts[2];
                    double value = Double.parseDouble(parts[3]);

                    storage.addPatientData(patientId, value, label, timestamp);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read data from file: " + filePath);
            e.printStackTrace();
        }
    }
}
