package app.security;
import app.models.User;

public class SessionManager {

    private static SessionManager instance;
    private User loggedUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(User user) {
        this.loggedUser = user;
    }

    public void logout() {
        this.loggedUser = null;
    }

    public User getLoggedUser() {
        return loggedUser;
    }
}

