package app.models;

import java.time.LocalDateTime;

public class AppointmentBuilder {
    private int id;
    private Doctor doctor;
    private Patient patient;
    private LocalDateTime dateTime;
    private String status;
    private String notes;

    public AppointmentBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public AppointmentBuilder setDoctor(Doctor doctor) {
        this.doctor = doctor;
        return this;
    }

    public AppointmentBuilder setPatient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public AppointmentBuilder setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public AppointmentBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public AppointmentBuilder setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public Appointment build() {
        return new Appointment(id, doctor, patient, dateTime, status, notes);
    }
}
