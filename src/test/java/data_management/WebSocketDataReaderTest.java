package data_management;

import com.data_management.DataStorage;
import com.data_management.WebSocketDataReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketDataReaderTest {
    private MockServer server;
    private int port = 8765;
    @BeforeEach
    public void setup() throws InterruptedException{
        server = new MockServer(new InetSocketAddress("localhost", port));
        server.start();
        Thread.sleep(500); //gives server time to bind
    }
    @AfterEach
    public void teardown() throws InterruptedException{server.stop(100);}
    @Test
    public void testValidDataStream() throws Exception{
        DataStorage storage = new DataStorage();
        WebSocketDataReader reader = new WebSocketDataReader();
        //Starts reader in background
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try{reader.readData(storage, "ws://localhost: " + port);}
            catch(Exception e){fail("Reader threw: " + e.getMessage());}
        });
        //Waits until client connects and sends valid data
        server.waitForConnection();
        server.sendMessage("1,1710000000000,HeartRate,78.5");
        Thread.sleep(500); //gives time to store
        assertEquals(1, storage.getAllPatients().size());
        assertEquals(1, storage.getRecords(1, Long.MIN_VALUE, Long.MAX_VALUE).size());
        server.broadcastClose("Done");
        executor.shutdownNow();
    }
    @Test
    public void testMalformedMessageIgnored() throws Exception{
        DataStorage storage = new DataStorage();
        WebSocketDataReader reader = new WebSocketDataReader();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try{reader.readData(storage, "ws://localhost: " + port);}
            catch(Exception e){fail("Reader threw: " + e.getMessage());}
        });
        server.waitForConnection();
        server.sendMessage("invalid,data");
        Thread.sleep(300);
        assertTrue(storage.getAllPatients().isEmpty());
        server.broadcastClose("Closing after malformed");
        executor.shutdownNow();
    }
}
