package app.services.booking;

import app.models.Doctor;
import app.models.Appointment;
import java.time.LocalDateTime;
import java.util.List;

public class StandardBookingStrategy implements BookingValidationStrategy {

    @Override
    public boolean isValid(Doctor doctor, LocalDateTime time, List<Appointment> existingAppointments) {
        // Simple double-booking check:
        // Iterate through all existing appointments.
        // If an appointment exists for THIS doctor at THIS time, return false.

        for (Appointment appt : existingAppointments) {
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
}
