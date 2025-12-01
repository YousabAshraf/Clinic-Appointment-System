package app.factory;
import app.models.Admin;
import app.models.Doctor;
import app.models.Patient;
import app.models.User;

public class UserFactory {

    public static User createUser( int id, String name, String email, String password,String role) {
        System.out.println(id);
        switch (role.toUpperCase()) {
            case "PATIENT":
                return new Patient(id, name, email, password);
            case "DOCTOR":
                return new Doctor(id, name, email, password);
            case "ADMIN":
                return new Admin(id, name, email, password);
            default:
                throw new IllegalArgumentException("Invalid role");
        }
    }
}

