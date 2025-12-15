package app.factory;
import app.models.Admin;
import app.models.DoctorBuilder;
import app.models.Patient;
import app.models.User;

public class UserFactory {

    public static User createUser( int id, String name, String email, String password, String role) {
        System.out.println(id);
        switch (role.toUpperCase()) {
            case "PATIENT":
                return new Patient(id, name, email, password);
            case "DOCTOR":
                return new DoctorBuilder(id, name, email, password).build();
            case "ADMIN":
                return new Admin(id, name, email, password);
            default:
                throw new IllegalArgumentException("Invalid role");
        }
    }
}

