package app.services;

import app.models.Appointment;
import app.models.AppointmentBuilder;
import app.models.Doctor;
import app.models.Patient;
import app.services.booking.BookingValidationStrategy;
import app.services.booking.StandardBookingStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentScheduler {
    private static AppointmentScheduler instance;
    private List<Appointment> appointments;
    private BookingValidationStrategy validationStrategy;

    private AppointmentScheduler() {
        this.appointments = new ArrayList<>();
        this.validationStrategy = new StandardBookingStrategy(); // Default strategy
    }

    public static AppointmentScheduler getInstance() {
        if (instance == null) {
            instance = new AppointmentScheduler();
        }
        return instance;
    }

    public void setValidationStrategy(BookingValidationStrategy strategy) {
        this.validationStrategy = strategy;
    }

    public boolean bookAppointment(Doctor doctor, Patient patient, LocalDateTime dateTime) {
        System.out.println("Attempting to book for Dr. " + doctor.getName() + " at " + dateTime + "...");

        if (!validationStrategy.isValid(doctor, dateTime, appointments)) {
            System.out.println("Booking denied: Validation failed.");
            return false;
        }
        Appointment newAppointment = new AppointmentBuilder()
                .setId(appointments.size() + 1)
                .setDoctor(doctor)
                .setPatient(patient)
                .setDateTime(dateTime)
                .setStatus("CONFIRMED")
                .build();

        appointments.add(newAppointment);
        System.out.println("Booking Successful: " + newAppointment);
        return true;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }
}
