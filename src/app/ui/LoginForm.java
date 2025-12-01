package app.ui;

import javax.swing.*;
import java.awt.*;
import app.services.LoginService;
import app.services.RegistrationService;
import app.models.User;

public class LoginForm extends JFrame {



    public LoginForm() {



        setTitle("Clinic Appointment System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("Welcome to Clinic Appointment System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(title, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passLabel);
        formPanel.add(passField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Create Account");
        loginBtn.setPreferredSize(new Dimension(120, 30));
        registerBtn.setPreferredSize(new Dimension(150, 30));

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Button actions
        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String pass = new String(passField.getPassword());
            User user =  LoginService.getInstance().login(email, pass);


            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                new Dashboard().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Email or Password!");
            }
        });
        registerBtn.addActionListener(e -> new RegisterForm().setVisible(true));
    }
}
