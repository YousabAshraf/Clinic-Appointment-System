package app.ui;

import javax.swing.*;
import java.awt.*;
import app.services.LoginService;
import app.models.User;
import app.ui.style.Theme;

public class LoginForm extends JFrame {

    public LoginForm() {

        setTitle("Clinic Appointment System - Login");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Theme.BACKGROUND_COLOR);

        // Title
        JLabel title = new JLabel("Welcome Back", JLabel.CENTER);
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.PRIMARY_COLOR);
        mainPanel.add(title, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        formPanel.setBackground(Theme.BACKGROUND_COLOR);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(Theme.REGULAR_FONT);
        JTextField emailField = new JTextField();
        emailField.setFont(Theme.REGULAR_FONT);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(Theme.REGULAR_FONT);
        JPasswordField passField = new JPasswordField();
        passField.setFont(Theme.REGULAR_FONT);

        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passLabel);
        formPanel.add(passField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(Theme.BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(Theme.BUTTON_FONT);
        loginBtn.setBackground(Theme.PRIMARY_COLOR);
        loginBtn.setForeground(Theme.WHITE);
        loginBtn.setFocusPainted(false);
        // On Windows, setBackground might not work without this for some LookAndFeels,
        // but usually fine.

        JButton registerBtn = new JButton("Create Account");
        registerBtn.setFont(Theme.BUTTON_FONT);
        registerBtn.setBackground(Theme.SECONDARY_COLOR);
        registerBtn.setForeground(Theme.WHITE);
        registerBtn.setFocusPainted(false);

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Button actions
        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String pass = new String(passField.getPassword());
            User user = LoginService.getInstance().login(email, pass);

            if (user != null) {
                // JOptionPane.showMessageDialog(this, "Login Successful!");
                new Dashboard().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Email or Password!", "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        registerBtn.addActionListener(e -> {
            new RegisterForm().setVisible(true);
            this.dispose();
        });
    }
}
