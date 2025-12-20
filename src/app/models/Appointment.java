package app.models;

import app.models.appointmentState.AppointmentState;
import app.models.appointmentState.PendingState;

import java.time.LocalDateTime;

public class Appointment {

    private int id;
    private Doctor doctor;
    private Patient patient;
    private LocalDateTime dateTime;
    private AppointmentState state;
    private String notes;

    public Appointment(int id, Doctor doctor, Patient patient,
            LocalDateTime dateTime, String status, String notes) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.dateTime = dateTime;
        this.notes = notes;
        this.state = new PendingState();
    }

    public void approve() {
        state.approve(this);
    }

    public void cancel() {
        state.cancel(this);
    }

    public boolean isBlocked() {
        return patient != null && patient.getId() == -1;
    }

    public void unblock() {
        if (isBlocked()) {
            this.patient = null;
        }
    }

    public void reschedule(LocalDateTime newTime) {
        state.reschedule(this, newTime);
    }

    public void setState(AppointmentState state) {
        this.state = state;
    }

    public String getStatus() {
        return state.getStatus();
    }

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

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return "Appointment [ID=" + id +
                ", Doctor=" + doctor.getName() +
                ", Patient=" + patient.getName() +
                ", Time=" + dateTime +
                ", Status=" + getStatus() + "]";
    }
}
