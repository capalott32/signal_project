package data_management;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * helper class for DataReader unit test
 */
public class MockServer extends WebSocketServer{
    private final CountDownLatch connectionLatch = new CountDownLatch(1);
    private WebSocket connectedClient;
    public MockServer(InetSocketAddress address){super(address);}
    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        this.connectedClient = webSocket;
        connectionLatch.countDown();
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {}

    @Override
    public void onMessage(WebSocket webSocket, String s) {}

    @Override
    public void onError(WebSocket webSocket, Exception e) {e.printStackTrace();}

    @Override
    public void onStart() {System.out.println("Mock server started");}
    public void waitForConnection() throws InterruptedException{connectionLatch.await();}
    public void sendMessage(String message){
        if(connectedClient != null) connectedClient.send(message);
    }
    public void broadcastClose(String reason){
        if(connectedClient != null) connectedClient.close(1000, reason);
    }
}