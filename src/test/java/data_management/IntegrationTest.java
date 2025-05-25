package data_management;

import com.data_management.DataIngestionMain;
import org.junit.jupiter.api.Test;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    @Test
    void testFileModeEndToEnd() throws Exception {
        // 1) capture stdout
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(buf));

        try {
            // 2) invoke your main in FILE mode against sample.csv
            DataIngestionMain.main(new String[]{"file", "sample.csv"});
        } finally {
            // restore stdout
            System.setOut(oldOut);
        }

        String out = buf.toString();

        // 3) assert that key lines appear
        assertTrue(out.contains("SystolicPressure"),
                "Should mention SystolicPressure in the dump: " + out);
        assertTrue(out.contains("DiastolicPressure"),
                "Should mention DiastolicPressure in the dump: " + out);
    }
}
