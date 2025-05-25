package data_management;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.data_management.DataIngestionMain;
import org.junit.jupiter.api.Test;

public class IntegrationTest {

    @Test
    public void testFileModeEndToEnd() throws Exception {
        // 1) Capture stdout
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(buf));

        // 2) Invoke your main in file mode
        DataIngestionMain.main(new String[] { "file", "sample.csv" });

        // 3) Restore stdout
        System.setOut(oldOut);
        String out = buf.toString();

        // 4) Basic sanity checks
        assertTrue(out.contains("----- All patients and their records -----"),
                "Should print the patient dump header");
        assertTrue(out.contains("Patient 1:"), "Should show Patient 1");
        assertTrue(out.contains("SystolicPressure = 200.00"), "Should list the Systolic record");
        assertTrue(out.contains("DiastolicPressure = 50.00"), "Should list the Diastolic record");
        assertTrue(out.contains("----- Evaluating alerts -----"), "Should run alert evaluation");
        // Depending on your trigger rules, you might see no alerts or a specific message
        // at least ensure the “complete” message shows up:
        assertTrue(out.contains("Data ingestion & alert evaluation complete"),
                "Should finish with our completion banner");
    }
}
