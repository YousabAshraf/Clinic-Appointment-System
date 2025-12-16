package app.models.appointment.state;

import app.models.Appointment;
import java.time.LocalDateTime;

public class PendingState implements AppointmentState {

    @Override
    public void approve(Appointment appointment) {
        appointment.setState(new ApprovedState());
        System.out.println("Appointment approved.");
    }

    @Override
    public void cancel(Appointment appointment) {
        appointment.setState(new CancelledState());
        System.out.println("Appointment cancelled.");
    }

    @Override
    public void reschedule(Appointment appointment, LocalDateTime newTime) {
        appointment.setDateTime(newTime);
        System.out.println("Pending appointment rescheduled.");
    }

    @Override
    public String getStatus() {
        return "PENDING";
    }
}
