package app.ui;

import javax.swing.*;
import java.awt.*;
import app.services.RegistrationService;
import app.services.DoctorService;
import app.ui.style.Theme;

public class RegisterForm extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passField;
    private JComboBox<String> roleBox;

    private JLabel specialtyLabel;
    private JTextField specialtyField;
    private JLabel feeLabel;
    private JTextField feeField;

    public RegisterForm() {
        setTitle("Create Account - Clinic Appointment System");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel container = new JPanel(new GridLayout(1, 2));

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Theme.PRIMARY_COLOR);

        ImageIcon icon = new ImageIcon("src/resources/images/login_bg.png");
        Image img = icon.getImage().getScaledInstance(475, 600, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(img));
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        container.add(imagePanel);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Theme.BACKGROUND_COLOR);

        JLabel title = new JLabel("Create Your Account", JLabel.CENTER);
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.PRIMARY_COLOR);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 15));
        formPanel.setBackground(Theme.BACKGROUND_COLOR);

        addLabeledField(formPanel, "Name", nameField = new JTextField());
        addLabeledField(formPanel, "Email", emailField = new JTextField());
        addLabeledField(formPanel, "Password", passField = new JPasswordField());

        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(Theme.REGULAR_FONT);
        formPanel.add(roleLabel);

        String[] roles = { "PATIENT", "DOCTOR", "ADMIN" };
        roleBox = new JComboBox<>(roles);
        roleBox.setFont(Theme.REGULAR_FONT);
        roleBox.setBackground(Theme.WHITE);
        formPanel.add(roleBox);

        specialtyLabel = new JLabel("Specialty");
        specialtyLabel.setFont(Theme.REGULAR_FONT);
        specialtyField = new JTextField();
        specialtyField.setFont(Theme.REGULAR_FONT);

        feeLabel = new JLabel("Consultation Fee");
        feeLabel.setFont(Theme.REGULAR_FONT);
        feeField = new JTextField();
        feeField.setFont(Theme.REGULAR_FONT);

        formPanel.add(specialtyLabel);
        formPanel.add(specialtyField);
        formPanel.add(feeLabel);
        formPanel.add(feeField);

        toggleDoctorFields(false);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(Theme.BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton createBtn = new JButton("CREATE ACCOUNT");
        Theme.styleButton(createBtn, true);

        JButton backBtn = new JButton("Back to Login");
        Theme.styleButton(backBtn, false);

        buttonPanel.add(createBtn);
        buttonPanel.add(backBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        container.add(mainPanel);

        add(container);

        roleBox.addActionListener(e -> {
            String selected = (String) roleBox.getSelectedItem();
            toggleDoctorFields("DOCTOR".equals(selected));
        });

        createBtn.addActionListener(e -> handleRegistration());

        backBtn.addActionListener(e -> {
            new LoginForm().setVisible(true);
            this.dispose();
        });
    }

    private void addLabeledField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.SUBHEADER_FONT);
        label.setForeground(Theme.TEXT_SECONDARY);
        panel.add(label);

        field.setFont(Theme.REGULAR_FONT);
        if (field instanceof JTextField) { // Includes JPasswordField
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        }
        panel.add(field);
    }

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

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all standard fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = false;

        if ("DOCTOR".equals(role)) {
            String spec = specialtyField.getText();
            String feeText = feeField.getText();

            if (spec.isEmpty() || feeText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in Specialty and Fee!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double fee = Double.parseDouble(feeText);
                success = DoctorService.getInstance().addDoctor(name, email, pass, spec, fee);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Fee must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

        } else {
            success = RegistrationService.getInstance().register(name, email, pass, role);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Account Created Successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            new LoginForm().setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Email already used!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
