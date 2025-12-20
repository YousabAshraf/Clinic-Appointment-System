package app.models.appointmentState;

import app.models.Appointment;
import java.time.LocalDateTime;

public class ApprovedState implements AppointmentState {

    @Override
    public void approve(Appointment appointment) {
        System.out.println("Appointment already approved.");
    }

    @Override
    public void cancel(Appointment appointment) {
        appointment.setState(new CancelledState());
        System.out.println("Approved appointment cancelled.");
    }

    @Override
    public void reschedule(Appointment appointment, LocalDateTime newTime) {
        appointment.setDateTime(newTime);
        appointment.setState(new PendingState());
        System.out.println("Approved appointment rescheduled.");
    }

    @Override
    public String getStatus() {
        return "APPROVED";
    }
}
