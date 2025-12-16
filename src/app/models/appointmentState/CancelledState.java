package app.models.appointmentState;

import app.models.Appointment;
import java.time.LocalDateTime;

public class CancelledState implements AppointmentState {

    @Override
    public void approve(Appointment appointment) {
        System.out.println("Cancelled appointment cannot be approved.");
    }

    @Override
    public void cancel(Appointment appointment) {
        System.out.println("Appointment already cancelled.");
    }

    @Override
    public void reschedule(Appointment appointment, LocalDateTime newTime) {
        System.out.println("Cancelled appointment cannot be rescheduled.");
    }

    @Override
    public String getStatus() {
        return "CANCELLED";
    }
}
