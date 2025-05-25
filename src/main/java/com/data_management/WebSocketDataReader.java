package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

/*
  Reads a live CSV‐stream from a WebSocket server and feeds it into DataStorage.
 */
public class WebSocketDataReader implements DataReader {

    @Override
    public void readData(DataStorage storage, String wsUrl) throws IOException {
        URI serverUri = URI.create(wsUrl);
        CountDownLatch latch = new CountDownLatch(1);

        WebSocketClient client = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("WebSocket connected to " + wsUrl);
            }

            @Override
            public void onMessage(String message) {
                //Parse comma separated message: patientId, timestamp, label, value
                String[] parts = message.split(",");
                if (parts.length != 4) return; //Skip malformed messages
                try {
                    //Attempt to parse and store incoming data
                    int    patientId = Integer.parseInt(parts[0]);
                    long   timestamp = Long.parseLong(parts[1]);
                    String label     = parts[2];
                    double value     = Double.parseDouble(parts[3]);
                    storage.addPatientData(patientId, value, label, timestamp);
                } catch (NumberFormatException e) {
                    //Handle corrupted or non-numeric data
                    System.err.println("Malformed data: " + message);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("WebSocket closed: " + reason);
                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        client.connect(); //asynchronous connect call
        try {
            latch.await(); //block until socket is closed
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for WS to close", e);
        } finally {
            client.close();
        }
    }
}
