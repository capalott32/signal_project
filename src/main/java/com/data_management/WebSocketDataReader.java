package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

/**
 * Reads a live CSV‐stream from a WebSocket server and feeds it into DataStorage.
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
                String[] parts = message.split(",");
                if (parts.length != 4) return;
                try {
                    int    patientId = Integer.parseInt(parts[0]);
                    long   timestamp = Long.parseLong(parts[1]);
                    String label     = parts[2];
                    double value     = Double.parseDouble(parts[3]);
                    storage.addPatientData(patientId, value, label, timestamp);
                } catch (NumberFormatException e) {
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

        client.connect();
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for WS to close", e);
        } finally {
            client.close();
        }
    }
}
