package app.models;

import java.util.ArrayList;
import java.util.List;

public class DoctorBuilder {
    // These fields hold the data temporarily
    private int id;
    private String name;
    private String email;
    private String password;
    private String specialty = "General";
    private double fee = 50.0;
    private List<String> availability = new ArrayList<>();

    public DoctorBuilder(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Fluent Setters (return 'this')
    public DoctorBuilder setSpecialty(String specialty) {
        this.specialty = specialty;
        return this;
    }

    public DoctorBuilder setFee(double fee) {
        this.fee = fee;
        return this;
    }

    public DoctorBuilder addAvailability(String slot) {
        this.availability.add(slot);
        return this;
    }

    // Getters (So the Doctor class can read the data)
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getSpecialty() { return specialty; }
    public double getFee() { return fee; }
    public List<String> getAvailability() { return availability; }

    public Doctor build() {
        return new Doctor(this);
    }
}
