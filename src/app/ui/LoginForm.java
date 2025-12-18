package app.ui;

import javax.swing.*;
import java.awt.*;
import app.services.LoginService;
import app.models.User;
import app.ui.style.Theme;

public class LoginForm extends JFrame {

    public LoginForm() {

        setTitle("Clinic Appointment System - Login");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Container
        JPanel container = new JPanel(new GridLayout(1, 2));

        // --- Left Side (Image) ---
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Theme.PRIMARY_COLOR);

        // Load and Scale Image
        ImageIcon icon = new ImageIcon("src/resources/images/login_bg.png");
        Image img = icon.getImage().getScaledInstance(450, 550, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(img));
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        container.add(imagePanel);

        // --- Right Side (Form) ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        mainPanel.setBackground(Theme.BACKGROUND_COLOR);

        // Title
        JLabel title = new JLabel("Welcome Back", JLabel.CENTER);
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.PRIMARY_COLOR);
        mainPanel.add(title, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        formPanel.setBackground(Theme.BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(Theme.SUBHEADER_FONT);
        emailLabel.setForeground(Theme.TEXT_SECONDARY);
        JTextField emailField = new JTextField();
        emailField.setFont(Theme.REGULAR_FONT);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(Theme.SUBHEADER_FONT);
        passLabel.setForeground(Theme.TEXT_SECONDARY);
        JPasswordField passField = new JPasswordField();
        passField.setFont(Theme.REGULAR_FONT);
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passLabel);
        formPanel.add(passField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(Theme.BACKGROUND_COLOR);

        JButton loginBtn = new JButton("LOGIN");
        Theme.styleButton(loginBtn, true);

        JButton registerBtn = new JButton("Create New Account");
        Theme.styleButton(registerBtn, false);

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        container.add(mainPanel);

        add(container);

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
