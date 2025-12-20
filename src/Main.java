import app.services.*;
import app.ui.LoginForm;

public class Main {
    public static void main(String[] args) {
        // Initialize Data
        initializeData();

        // Launch GUI
        new LoginForm().setVisible(true);
    }

    private static void initializeData() {
        System.out.println("Initializing Seed Data...");

        RegistrationService.getInstance().register("System Admin", "admin@cas.com", "admin123", "ADMIN");

        String[] specialties = { "Cardiology", "Dermatology", "Neurology", "Pediatrics", "Orthopedics" };
        DoctorService docService = DoctorService.getInstance();

        for (int i = 1; i <= 10; i++) {
            String name = "Dr. " + (char) ('A' + i - 1) + " House";
            String email = "doctor" + i + "@cas.com";
            String specialty = specialties[i % specialties.length];
            double fee = 50.0 + (i * 10);

            docService.addDoctor(name, email, "pass123", specialty, fee);
        }

        System.out.println("Data Initialization Complete.");
    }
}