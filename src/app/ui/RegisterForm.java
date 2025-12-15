package app.ui;

import javax.swing.*;
import java.awt.*;
import app.services.RegistrationService;
import app.services.DoctorService; // Import your new service

public class RegisterForm extends JFrame {
    // Declare fields as class variables so we can access them in listeners
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passField;
    private JComboBox<String> roleBox;

    // Extra fields for Doctor
    private JLabel specialtyLabel;
    private JTextField specialtyField;
    private JLabel feeLabel;
    private JTextField feeField;

    public RegisterForm() {
        setTitle("Create Account - Clinic Appointment System");
        setSize(450, 450); // Increased height for extra fields
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("Create Your Account", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(title, BorderLayout.NORTH);

        // Form panel (Grid Layout)
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10)); // Increased rows

        // Standard Fields
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));
        passField = new JPasswordField();
        formPanel.add(passField);

        formPanel.add(new JLabel("Select Role:"));
        String[] roles = {"PATIENT", "DOCTOR", "ADMIN"};
        roleBox = new JComboBox<>(roles);
        formPanel.add(roleBox);

        // DOCTOR SPECIFIC FIELDS (Hidden by default)
        specialtyLabel = new JLabel("Specialty:");
        specialtyField = new JTextField();
        feeLabel = new JLabel("Consultation Fee:");
        feeField = new JTextField();

        formPanel.add(specialtyLabel);
        formPanel.add(specialtyField);
        formPanel.add(feeLabel);
        formPanel.add(feeField);

        // Hide them initially
        toggleDoctorFields(false);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createBtn = new JButton("Create Account");
        createBtn.setPreferredSize(new Dimension(180, 30));
        buttonPanel.add(createBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // LISTENERS

        // Dropdown Listener: Show/Hide fields based on role
        roleBox.addActionListener(e -> {
            String selected = (String) roleBox.getSelectedItem();
            toggleDoctorFields("DOCTOR".equals(selected));
        });

        // Button Listener: Handle logic
        createBtn.addActionListener(e -> handleRegistration());
    }

    // Helper to show/hide fields
    private void toggleDoctorFields(boolean show) {
        specialtyLabel.setVisible(show);
        specialtyField.setVisible(show);
        feeLabel.setVisible(show);
        feeField.setVisible(show);
    }

    private void handleRegistration() {
        String name = nameField.getText();
        String email = emailField.getText();
        String pass = new String(passField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        boolean success = false;

        if ("DOCTOR".equals(role)) {
            // Validate Doctor inputs
            String spec = specialtyField.getText();
            String feeText = feeField.getText();

            if (spec.isEmpty() || feeText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in Specialty and Fee!");
                return;
            }

            try {
                double fee = Double.parseDouble(feeText);
                // USE YOUR NEW SERVICE!
                success = DoctorService.getInstance().addDoctor(name, email, pass, spec, fee);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Fee must be a valid number!");
                return;
            }

        } else {
            // Normal Registration for Patient/Admin
            success = RegistrationService.getInstance().register(name, email, pass, role);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Account Created Successfully!");
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Email already used!");
        }
    }
}
