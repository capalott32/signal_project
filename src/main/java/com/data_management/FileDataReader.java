package com.data_management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileDataReader implements DataReader {
    private final DataStorage storage;

    // this is the one we need:
    public FileDataReader(DataStorage storage) {
        this.storage = storage;
    }

    @Override
    public void readData(DataStorage storage, String path) throws IOException {
        // your CSV‐parsing logic here, using 'path' instead of the old single‐arg.
        try (BufferedReader r = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int    id    = Integer.parseInt(parts[0]);
                    long   ts    = Long.parseLong(parts[1]);
                    String lbl   = parts[2];
                    double val   = Double.parseDouble(parts[3]);
                    storage.addPatientData(id, val, lbl, ts);
                }
            }
        }
    }

    // We no longer need the old single‐arg method; remove it.
}
