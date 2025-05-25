package com.data_management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a patient and manages their medical records.
 * This class stores patient-specific data, allowing for the addition and
 * retrieval
 * of medical records based on specified criteria.
 */
public class Patient {
    private final int patientId;
    private final List<PatientRecord> patientRecords = Collections.synchronizedList(new ArrayList<>());

    /**
     * Constructs a new Patient with a specified ID.
     * Initializes an empty list of patient records.
     *
     * @param patientId the unique identifier for the patient
     */
    public Patient(int patientId) {
        this.patientId = patientId;
    }

    /**
     * Adds a new record to this patient's list of medical records.
     * The record is created with the specified measurement value, record type, and
     * timestamp.
     *
     * @param measurementValue the measurement value to store in the record
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since UNIX epoch
     */
    public void addRecord(double measurementValue, String recordType, long timestamp) {
        synchronized(patientRecords){
            boolean duplicate = patientRecords.stream().anyMatch(record -> record.getTimestamp() == timestamp && record.getRecordType().equalsIgnoreCase(recordType));
            if(!duplicate) patientRecords.add(new PatientRecord(patientId, measurementValue, recordType, timestamp));
        }
    }

    /**
     * Retrieves a list of PatientRecord objects for this patient that fall within a
     * specified time range.
     * The method filters records based on the start and end times provided.
     *
     * @param startTime the start of the time range, in milliseconds since UNIX
     *                  epoch
     * @param endTime   the end of the time range, in milliseconds since UNIX epoch
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public List<PatientRecord> getRecords(long startTime, long endTime) {
        List<PatientRecord> filteredRecords = new ArrayList<>();
        synchronized(patientRecords){
            for(PatientRecord record: patientRecords){
                long timestamp = record.getTimestamp();
                if(timestamp >= startTime && timestamp <= endTime) filteredRecords.add(record);
            }
        }
        return filteredRecords;
    }
    public int getPatientId(){return patientId;}
}
