package app.models;

import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private Doctor doctor;
    private Patient patient;
    private LocalDateTime dateTime;
    private String status;
    private String notes;

    public Appointment(int id, Doctor doctor, Patient patient, LocalDateTime dateTime, String status, String notes) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.dateTime = dateTime;
        this.status = status;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return "Appointment [ID=" + id + ", Doctor=" + doctor.getName() + ", Patient=" + patient.getName() +
                ", Time=" + dateTime + ", Status=" + status + "]";
    }
}
