package data_management;

import com.data_management.DataStorage;
import com.data_management.HealthDataWebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HealthDataWebSocketClientTest {
    @Test
    public void testValidMessageStored() throws Exception{
        DataStorage storage = new DataStorage();
        URI fakeUri = new URI("ws://localhost:1234");
        HealthDataWebSocketClient client = new HealthDataWebSocketClient(fakeUri.toString(), storage){
            @Override public void onOpen(ServerHandshake handshake){}
            @Override public void onClose(int code, String reason, boolean remote){}
            @Override public void onError(Exception ex){}
        };
        String message = "1,1710000000000,HeartRate,78.5";
        client.onMessage(message);
        Assertions.assertEquals(1, storage.getAllPatients().size());
        Assertions.assertEquals(1, storage.getRecords(1, Long.MIN_VALUE, Long.MAX_VALUE).size());
    }
    @Test
    public void testInvalidMessageFormat() throws Exception{
        DataStorage storage = new DataStorage();
        HealthDataWebSocketClient client = new HealthDataWebSocketClient("ws://localhost:1234", storage){
            @Override public void onOpen(ServerHandshake handshake){}
            @Override public void onClose(int code, String reason, boolean remote){}
            @Override public void onError(Exception ex){}
        };
        String malformedMessage = "bad,input";
        client.onMessage(malformedMessage);
        assertTrue(storage.getAllPatients().isEmpty(), "No patients should be added");
    }
    @Test
    public void testNumberFormatExceptionHandling() throws Exception{
        DataStorage storage = new DataStorage();
        HealthDataWebSocketClient client = new HealthDataWebSocketClient("ws://localhost:1234", storage){
            @Override public void onOpen(ServerHandshake handshake){}
            @Override public void onClose(int code, String reason, boolean remote){}
            @Override public void onError(Exception ex){}
        };
        String invalidNumericMessage = "1,abs,Oxygen,95.0";
        assertDoesNotThrow(() -> client.onMessage(invalidNumericMessage));
    }
}
