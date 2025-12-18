package app.services.appointment;

import app.models.Appointment;
import app.models.AppointmentBuilder;
import app.models.Doctor;
import app.models.Patient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;
import java.util.stream.Collectors;

public class AppointmentScheduler {
    private static AppointmentScheduler instance;
    private List<Appointment> appointments;

    // Private constructor (Singleton)
    private AppointmentScheduler() {
        this.appointments = new ArrayList<>();
    }

    // Public access point
    public static AppointmentScheduler getInstance() {
        if (instance == null) {
            instance = new AppointmentScheduler();
        }
        return instance;
    }

    public boolean bookAppointment(Doctor doctor, Patient patient, LocalDateTime dateTime) {
        System.out.println("Attempting to book for Dr. " + doctor.getName() + " at " + dateTime + "...");

        // 1. Validate Doctor Availability
        if (!isSlotAvailable(doctor, dateTime)) {
            System.out.println("Booking denied: Doctor unavailable.");
            return false;
        }

        // 2. Validate Patient Availability (Prevent Patient Double Booking)
        if (!isPatientAvailable(patient, dateTime)) {
            System.out.println("Booking denied: Patient already has an appointment at this time.");
            return false;
        }

        // 3. Build using Builder
        Appointment newAppointment = new AppointmentBuilder()
                .setId(appointments.size() + 1)
                .setDoctor(doctor)
                .setPatient(patient)
                .setDateTime(dateTime)
                .build();

        // 4. Save
        appointments.add(newAppointment);
        System.out.println("Booking Successful: " + newAppointment);
        return true;
    }

    private boolean isSlotAvailable(Doctor doctor, LocalDateTime time) {
        for (Appointment appt : appointments) {
            // Ignore cancelled appointments
            if ("Cancelled".equalsIgnoreCase(appt.getStatus()) || "Rejected".equalsIgnoreCase(appt.getStatus())) {
                continue;
            }

            if (appt.getDoctor().getId() == doctor.getId()) {
                if (appt.getDateTime().isEqual(time)) {
                    System.out
                            .println("Validation Failed: Doctor " + doctor.getName() + " is already booked at " + time);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isPatientAvailable(Patient patient, LocalDateTime time) {
        for (Appointment appt : appointments) {
            // Ignore cancelled appointments
            if ("Cancelled".equalsIgnoreCase(appt.getStatus()) || "Rejected".equalsIgnoreCase(appt.getStatus())) {
                continue;
            }

            if (appt.getPatient().getId() == patient.getId()) {
                if (appt.getDateTime().isEqual(time)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isSlotBooked(int doctorId, LocalDateTime time) {
        for (Appointment appt : appointments) {
            // Ignore cancelled appointments
            if ("Cancelled".equalsIgnoreCase(appt.getStatus()) || "Rejected".equalsIgnoreCase(appt.getStatus())) {
                continue;
            }

            if (appt.getDoctor().getId() == doctorId) {
                if (appt.getDateTime().isEqual(time)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public List<Appointment> getDailySchedule(LocalDate date) {
        return appointments.stream()
                .filter(a -> a.getDateTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<Appointment> getWeeklySchedule(LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);

        return appointments.stream()
                .filter(a -> {
                    LocalDate d = a.getDateTime().toLocalDate();
                    return !d.isBefore(startDate) && !d.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

}
