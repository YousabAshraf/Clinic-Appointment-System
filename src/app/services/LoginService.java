package app.services;

import app.models.User;
import app.security.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class LoginService {

    private static LoginService instance;
    private List<User> users;

    private LoginService() {
        users = new ArrayList<>();
    }

    public static LoginService getInstance() {
        if (instance == null) {
            instance = new LoginService();
        }
        return instance;
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public User login(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
                SessionManager.getInstance().login(user);
                return user;
            }
        }
        return null;
    }
}
