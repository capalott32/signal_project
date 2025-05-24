package com;

import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;
import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {// allow switching between DataStorage and HealthDataSimulator

        if (args.length > 0 && args[0].equals("DataStorage")) {
            DataStorage.main(new String[]{});
        } else {
            HealthDataSimulator.main(new String[]{});
        }
    }

}
