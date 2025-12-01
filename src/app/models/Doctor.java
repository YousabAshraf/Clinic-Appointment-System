package app.models;

public class Doctor extends User {
    public Doctor(int id, String name, String email, String password) {
        super(id, name, email, password, "DOCTOR");
    }

    @Override
    public void openDashboard() {
        System.out.println("Opening Doctor Dashboard...");
    }
}

