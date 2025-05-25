package com.data_management;

import com.data_management.DataStorage;
import com.alerts.AlertGenerator;
import com.data_management.Patient;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

/*Reads a live stream of CSV‐formatted data from a WebSocket server.
  Each incoming message must be: patientId,timestamp,label,value
 */
/* first in one terminal run
java -cp "target\classes;target\dependency\*" com.cardio_generator.HealthDataSimulator --output websocket:8080

then open 2nd terminal and run
java -cp "target\classes;target\dependency\*" com.data_management.DataIngestionMain ws ws://localhost:8080
or
java ` -cp "target\classes;target\dependency\*" ` com.data_management.HealthDataWebSocketClient ws://localhost:8080
it will show the data*/

public class HealthDataWebSocketClient extends WebSocketClient {
    private final DataStorage storage;
    private final CountDownLatch closeLatch;

    public HealthDataWebSocketClient(String serverUri, DataStorage storage) throws Exception {
        super(new URI(serverUri));
        this.storage    = storage;
        this.closeLatch = new CountDownLatch(1);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("⟳ Connected to WebSocket server at " + getURI());
    }

    @Override
    public void onMessage(String message) {
        // Expecting CSV: patientId,timestamp,label,value
        String[] parts = message.split(",");
        if (parts.length != 4) {
            System.err.println("Invalid message format: " + message);
            return;
        }

        try {
            int    patientId = Integer.parseInt(parts[0].trim());
            long   ts        = Long.parseLong(parts[1].trim());
            String label     = parts[2].trim();
            String rawValue  = parts[3].trim();

            // strip off any trailing “%” (or really any non-digit/non-dot just to be safe)
            rawValue = rawValue.replaceAll("[^0-9.\\-]","");

            double value     = Double.parseDouble(rawValue);

            storage.addPatientData(patientId, value, label, ts);
            System.out.println(" Stored: " + message);
        } catch (NumberFormatException e) {
            System.err.println(" Number parse error on message: " + message);
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("⤫ WebSocket closed: " + reason);
        closeLatch.countDown();
    }

    @Override
    public void onError(Exception ex) {
        System.err.println(" WebSocket error:");
        ex.printStackTrace();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java com.data_management.HealthDataWebSocketClient ws://<host>:<port>");
            System.exit(1);
        }

        // 1) Set up storage & client
        DataStorage storage = new DataStorage();
        HealthDataWebSocketClient client =
                new HealthDataWebSocketClient(args[0], storage);

        // 2) Connect and block until the server closes the socket
        client.connectBlocking();
        client.closeLatch.await();

        // 3) Dump everything we received
        System.out.println("\n----- All patients and their records -----");
        for (var patient : storage.getAllPatients()) {
            System.out.println("Patient " + patient.getPatientId() + ":");
            for (var rec : patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE)) {
                System.out.printf("  [%d] %s = %.2f%n",
                        rec.getTimestamp(), rec.getRecordType(), rec.getMeasurementValue());
            }
        }

        // 4) Run alerts
        System.out.println("\n----- Evaluating alerts -----");
        AlertGenerator alertGen = new AlertGenerator(storage);
        for (Patient p : storage.getAllPatients()) {
            alertGen.evaluateData(p);
        }

        System.out.println("\n Data ingestion & alert evaluation complete.");
    }
}
