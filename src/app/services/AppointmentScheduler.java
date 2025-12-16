package app.services;


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

        // 1. Validate directly
        if (!isSlotAvailable(doctor, dateTime)) {
            System.out.println("Booking denied: Slot unavailable.");
            return false;
        }

        // 2. Build using Builder
        Appointment newAppointment = new AppointmentBuilder()
                .setId(appointments.size() + 1)
                .setDoctor(doctor)
                .setPatient(patient)
                .setDateTime(dateTime)
                .setStatus("PENDING")
                .build();

        // 3. Save
        appointments.add(newAppointment);
        System.out.println("Booking Successful: " + newAppointment);
        return true;
    }

    private boolean isSlotAvailable(Doctor doctor, LocalDateTime time) {
        for (Appointment appt : appointments) {
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
