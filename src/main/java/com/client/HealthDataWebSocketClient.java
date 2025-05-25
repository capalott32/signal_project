package com.client;

import com.data_management.DataStorage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class HealthDataWebSocketClient extends WebSocketClient {
    private final DataStorage dataStorage;
    public HealthDataWebSocketClient(String serverUri, DataStorage dataStorage) throws URISyntaxException {
        super(new URI(serverUri));
        this.dataStorage = dataStorage;
    }
    @Override
    public void onOpen(ServerHandshake serverHandshake) {System.out.println("Connected to WebSocket server");}
    @Override
    public void onMessage(String s) {
        try{
            String[] parts = s.split(",");
            if(parts.length == 4){
                int patientId = Integer.parseInt(parts[0]);
                long timestamp = Long.parseLong(parts[1]);
                String label = parts[2];
                double value = Double.parseDouble(parts[3]);
                dataStorage.addPatientData(patientId, value, label, timestamp);
                System.out.println("Data stored: " + s);
            }
            else System.err.println("Invalid message format: " + s);
        }
        catch (Exception e){
            System.err.println("Failed to process message: " + s);
            e.printStackTrace();
        }
    }
    @Override
    public void onClose(int i, String s, boolean b) {System.out.println("Disconnected from WebSocket server: " + s);}
    @Override
    public void onError(Exception e) {e.printStackTrace();}

    public static void main(String[] args) throws Exception{
        DataStorage storage = new DataStorage();
        HealthDataWebSocketClient client = new HealthDataWebSocketClient("ws://localhost:8080", storage);
    }
}
