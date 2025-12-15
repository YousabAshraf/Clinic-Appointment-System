package app.services.booking;

import app.models.Doctor;
import app.models.Appointment;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingValidationStrategy {
    boolean isValid(Doctor doctor, LocalDateTime time, List<Appointment> existingAppointments);
}
