import app.services.*;
import app.models.User;
import app.ui.LoginForm;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<User> users = new ArrayList<>();

        new LoginForm().setVisible(true);
    }
}