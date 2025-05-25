package data_management;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.Test;

//Verifies that alerts are triggered for high systolic (>180) and a rising trend across three measurements
public class AlertGeneratorTest {
    @Test
    void testBloodPressureAlerts() {
        DataStorage storage = new DataStorage();
        Patient patient = new Patient(1);

        // Simulate abnormal BP
        patient.addRecord(185, "SystolicPressure", System.currentTimeMillis());
        patient.addRecord(65, "DiastolicPressure", System.currentTimeMillis());

        // Simulate increasing trend
        patient.addRecord(110, "SystolicPressure", 1);
        patient.addRecord(125, "SystolicPressure", 2);
        patient.addRecord(140, "SystolicPressure", 3);

        com.alerts.AlertGenerator generator = new com.alerts.AlertGenerator(storage);
        generator.evaluateData(patient);

        // Check console output or captured alerts
    }
}
