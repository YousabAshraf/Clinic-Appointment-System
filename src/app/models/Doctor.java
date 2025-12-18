package app.models;

import java.util.List;

public class Doctor extends User {
    private String specialty;
    private double consultationFee;
    private List<String> availability;

    public Doctor(DoctorBuilder builder) {
        super(builder.getId(), builder.getName(), builder.getEmail(), builder.getPassword(), "DOCTOR");
        this.specialty = builder.getSpecialty();
        this.consultationFee = builder.getFee();
        this.availability = builder.getAvailability();
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double fee) {
        this.consultationFee = fee;
    }

    public List<String> getAvailability() {
        return availability;
    }

    public void addAvailability(String slot) {
        this.availability.add(slot);
    }

    public void setAvailability(List<String> availability) {
        this.availability = availability;
    }

    @Override
    public void openDashboard() {
        System.out.println("Opening Doctor Dashboard...");
    }
}