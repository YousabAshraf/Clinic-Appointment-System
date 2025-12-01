package app.models;

public class Patient extends User {
    public Patient(int id, String name, String email, String password) {
        super(id, name, email, password, "PATIENT");
    }

    @Override
    public void openDashboard() {        System.out.println("Opening Patient Dashboard...");
    }
}

