package app.ui;

import javax.swing.*;
import java.awt.*;
import app.services.RegistrationService;

public class RegisterForm extends JFrame {


    public RegisterForm() {


        setTitle("Create Account - Clinic Appointment System");
        setSize(450, 350);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("Create Your Account", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(title, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JLabel roleLabel = new JLabel("Select Role:");
        String[] roles = {"PATIENT", "DOCTOR", "ADMIN"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(emailLabel); formPanel.add(emailField);
        formPanel.add(passLabel); formPanel.add(passField);
        formPanel.add(roleLabel); formPanel.add(roleBox);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createBtn = new JButton("Create Account");
        createBtn.setPreferredSize(new Dimension(180, 30));
        buttonPanel.add(createBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        createBtn.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String pass = new String(passField.getPassword());
            String role = (String) roleBox.getSelectedItem();

            boolean created = RegistrationService.getInstance().register(name, email, pass, role);

            if (created) {
                JOptionPane.showMessageDialog(this, "Account Created Successfully!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Email already used!");
            }
        });
    }
}
