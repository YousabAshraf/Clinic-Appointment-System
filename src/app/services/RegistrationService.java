package app.services;

import app.models.User;
import app.factory.UserFactory;

import java.util.List;

public class RegistrationService {
    private int autoIncrementId = 1;

    private static RegistrationService instance;

    private RegistrationService() {
    }

    public static RegistrationService getInstance() {
        if (instance == null)
            instance = new RegistrationService();
        return instance;
    }

    public boolean register(String name, String email, String password, String role) {
        List<User> users = LoginService.getInstance().getUsers();
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return false;
            }
        }
        User newUser = UserFactory.createUser(autoIncrementId++, name, email, password, role);
        if (newUser == null) {
            return false;
        }
        LoginService.getInstance().addUser(newUser);

        return true;
    }
}
