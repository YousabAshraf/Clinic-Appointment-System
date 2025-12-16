package app.models.appointmentState;

import app.models.Appointment;
import java.time.LocalDateTime;

public interface AppointmentState {

    void approve(Appointment appointment);

    void cancel(Appointment appointment);

    void reschedule(Appointment appointment, LocalDateTime newTime);

    String getStatus();
}
