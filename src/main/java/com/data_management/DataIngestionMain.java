package com.data_management;

import com.alerts.AlertGenerator;

import java.io.IOException;

/*
  Entrypoint for batch or real-time ingest of CSV‐formatted health data.

 * Usage:
 1- just run it , will read sample.csv
 2- as follows ->
 first in one terminal run
 java -cp "target\classes;target\dependency\*" com.cardio_generator.HealthDataSimulator --output websocket:8080

 then open 2nd terminal and run
 java -cp "target\classes;target\dependency\*" com.data_management.DataIngestionMain ws ws://localhost:8080
 */
public class DataIngestionMain {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage:");
            System.err.println("  java com.data_management.DataIngestionMain file <path/to/data.csv>");
            System.err.println("  java com.data_management.DataIngestionMain ws   <ws://host:port>");
            System.exit(1);
        }

        String mode   = args[0];
        String source = args[1];

        DataStorage storage = new DataStorage();
        DataReader  reader;

        switch (mode) {
            case "file":
                // batch‐mode: read from a CSV on disk
                reader = new FileDataReader(storage);
                reader.readData(storage, source);
                break;

            case "ws":
                // real-time mode: connect to WebSocket server
                reader = new WebSocketDataReader();
                reader.readData(storage, source);
                break;

            default:
                System.err.println("Unknown mode: " + mode);
                System.exit(2);
        }
        System.out.println("----- All patients and their records -----");
        for (Patient p : storage.getAllPatients()) {
            System.out.println("Patient " + p.getPatientId() + ":");
            for (PatientRecord rec : p.getRecords(Long.MIN_VALUE, Long.MAX_VALUE)) {
                System.out.printf("  [%d] %s = %.2f%n",
                        rec.getTimestamp(), rec.getRecordType(), rec.getMeasurementValue());

            }

        }
        System.out.println("----- Evaluating alerts -----");
        AlertGenerator alertGen = new AlertGenerator(storage);
        for (Patient p : storage.getAllPatients()) {
            alertGen.evaluateData(p);
        }
        System.out.println("Alert evaluation finished.");


        System.out.println("Data ingestion finished.");
    }
}
