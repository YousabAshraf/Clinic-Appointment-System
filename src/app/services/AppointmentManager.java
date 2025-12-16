package app.services;

import app.models.Appointment;
import java.time.LocalDateTime;

public class AppointmentManager {

    public void approveAppointment(Appointment appointment) {
        appointment.approve();
    }

    public void cancelAppointment(Appointment appointment) {
        appointment.cancel();
    }

    public void rescheduleAppointment(Appointment appointment, LocalDateTime newTime) {
        appointment.reschedule(newTime);
    }
}
