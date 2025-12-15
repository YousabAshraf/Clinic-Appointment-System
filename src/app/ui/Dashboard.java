package app.ui;

import javax.swing.*;
import java.awt.*;
import app.security.SessionManager;
import app.models.User;
import app.ui.style.Theme;

public class Dashboard extends JFrame {

    public Dashboard() {
        User current = SessionManager.getInstance().getLoggedUser();
        // For development/testing, if no user is logged in, we might want to redirect.
        // But if this is just testing the GUI look, we can handle null gracefully or
        // redirect.
        if (current == null) {
            JOptionPane.showMessageDialog(this, "No user logged in!", "Security Warning", JOptionPane.WARNING_MESSAGE);
            new LoginForm().setVisible(true);
            this.dispose();
            return;
        }

        setTitle(current.getRole() + " Dashboard - Clinic Appointment System");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main Panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.BACKGROUND_COLOR);

        // --- Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel appTitle = new JLabel("Clinic System");
        appTitle.setFont(Theme.TITLE_FONT);
        appTitle.setForeground(Theme.WHITE);

        JLabel userLabel = new JLabel("User: " + current.getName() + " (" + current.getRole() + ")");
        userLabel.setFont(Theme.REGULAR_FONT);
        userLabel.setForeground(Theme.WHITE);

        headerPanel.add(appTitle, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Sidebar (Navigation) ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.WHITE);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebar.setPreferredSize(new Dimension(200, 0));

        addSidebarButton(sidebar, "Home");
        addSidebarButton(sidebar, "Appointments");
        addSidebarButton(sidebar, "Profile");
        if ("ADMIN".equalsIgnoreCase(current.getRole())) {
            addSidebarButton(sidebar, "Manage Users");
            addSidebarButton(sidebar, "Reports");
        }

        mainPanel.add(sidebar, BorderLayout.WEST);

        // --- Content Area ---
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Theme.BACKGROUND_COLOR);
        contentPanel.setLayout(new GridBagLayout()); // Center content for now

        JLabel welcomeLabel = new JLabel("Welcome to your Dashboard");
        welcomeLabel.setFont(Theme.HEADER_FONT);
        welcomeLabel.setForeground(Theme.TEXT_COLOR);
        contentPanel.add(welcomeLabel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // --- Footer / Actions ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Theme.WHITE);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(Theme.BUTTON_FONT);
        logoutBtn.setBackground(Theme.ERROR_COLOR);
        logoutBtn.setForeground(Theme.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            SessionManager.getInstance().logout();
            new LoginForm().setVisible(true);
            this.dispose();
        });

        footerPanel.add(logoutBtn);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addSidebarButton(JPanel panel, String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.REGULAR_FONT);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setBackground(Theme.SECONDARY_COLOR);
        btn.setForeground(Theme.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        panel.add(btn);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
    }
}
