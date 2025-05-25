package com.cardio_generator.outputs;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebSocketOutputStrategy implements OutputStrategy {

    private WebSocketServer server;
    private final ConcurrentLinkedQueue<WebSocket> connections = new ConcurrentLinkedQueue<>();

    public WebSocketOutputStrategy(int port) {
        server = new SimpleWebSocketServer(new InetSocketAddress(port), connections);
        System.out.println("WebSocket server created on port: " + port + ", listening for connections...");
        server.start();
    }

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
        // Broadcast the message to all connected clients
        for (WebSocket conn : connections) {
            try{conn.send(message);}
            catch(Exception e){
                System.err.println("failed to send message to WebSocket client: " + conn.getRemoteSocketAddress());
                e.printStackTrace();
                connections.remove(conn);
            }
        }
    }

    private static class SimpleWebSocketServer extends WebSocketServer {
        private final ConcurrentLinkedQueue<WebSocket> connections;
        public SimpleWebSocketServer(InetSocketAddress address, ConcurrentLinkedQueue<WebSocket> connections) {
            super(address);
            this.connections = connections;
        }

        @Override
        public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
            System.out.println("New connection: " + conn.getRemoteSocketAddress());
            connections.add(conn);
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
            connections.remove(conn);
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            // Not used in this context
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            System.err.println("WebSocket error on connection: " + (conn != null ? conn.getRemoteSocketAddress() : "Unknown") + ": " + ex.getMessage());
            if(conn != null) connections.remove(conn);
        }

        @Override
        public void onStart() {
            System.out.println("Server started successfully");
        }
    }
}
