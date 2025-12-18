package app.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import app.security.SessionManager;
import app.models.User;
import app.ui.style.Theme;

public class Dashboard extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private User currentUser;
    private Map<String, JButton> navButtons = new HashMap<>();

    public Dashboard() {
        currentUser = SessionManager.getInstance().getLoggedUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "No user logged in!", "Security Warning", JOptionPane.WARNING_MESSAGE);
            new LoginForm().setVisible(true);
            this.dispose();
            return;
        }

        setTitle(currentUser.getRole() + " Dashboard - Clinic Appointment System");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.BACKGROUND_COLOR);

        // --- Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel appTitle = new JLabel("Clinic System");
        appTitle.setFont(Theme.TITLE_FONT);
        appTitle.setForeground(Theme.WHITE);
        
        JLabel userLabel = new JLabel("User: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
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
        sidebar.setPreferredSize(new Dimension(220, 0));

        addSidebarButton(sidebar, "Home", e -> showCard("HOME"));
        
        if ("PATIENT".equalsIgnoreCase(currentUser.getRole())) {
            addSidebarButton(sidebar, "My Appointments", e -> showCard("APPOINTMENTS"));
            addSidebarButton(sidebar, "Book Appointment", e -> showCard("BOOKING"));
        } else if ("DOCTOR".equalsIgnoreCase(currentUser.getRole())) {
            addSidebarButton(sidebar, "My Schedule", e -> showCard("APPOINTMENTS"));
            // addSidebarButton(sidebar, "Availability", e -> showCard("AVAILABILITY"));
        } else if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            addSidebarButton(sidebar, "All Appointments", e -> showCard("APPOINTMENTS"));
            addSidebarButton(sidebar, "Manage Doctors", e -> showCard("DOCTORS"));
        }

        // Spacer
        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = new JButton("Logout");
        styleSidebarButton(logoutBtn);
        logoutBtn.setBackground(Theme.ERROR_COLOR);
        logoutBtn.addActionListener(e -> {
            SessionManager.getInstance().logout();
            new LoginForm().setVisible(true);
            this.dispose();
        });
        sidebar.add(logoutBtn);
        
        mainPanel.add(sidebar, BorderLayout.WEST);

        // --- Content Area (CardLayout) ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Theme.BACKGROUND_COLOR);

        // Add Panels
        contentPanel.add(createHomePanel(), "HOME");
        contentPanel.add(new app.ui.panels.ViewAppointmentsPanel(), "APPOINTMENTS");
        
        if ("PATIENT".equalsIgnoreCase(currentUser.getRole())) {
            contentPanel.add(new app.ui.panels.BookingPanel(), "BOOKING");
        }
        
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            contentPanel.add(new app.ui.panels.DoctorManagementPanel(), "DOCTORS");
        }

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void showCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
        // Here we could lazy-load or refresh data if needed
        System.out.println("Switched to: " + cardName);
    }

    private void addSidebarButton(JPanel panel, String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        styleSidebarButton(btn);
        btn.addActionListener(action);
        navButtons.put(text, btn);
        panel.add(btn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void styleSidebarButton(JButton btn) {
        btn.setFont(Theme.REGULAR_FONT);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setBackground(Theme.SECONDARY_COLOR);
        btn.setForeground(Theme.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel createHomePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.BACKGROUND_COLOR);
        JLabel l = new JLabel("Welcome, " + currentUser.getName() + "!");
        l.setFont(Theme.TITLE_FONT);
        l.setForeground(Theme.PRIMARY_COLOR);
        p.add(l);
        return p;
    }

    // These methods will be used to inject the real panels from external classes
    public void setAppointmentsPanel(JPanel panel) {
        contentPanel.add(panel, "APPOINTMENTS");
    }
    
    public void setBookingPanel(JPanel panel) {
        contentPanel.add(panel, "BOOKING");
    }

    public void setDoctorsPanel(JPanel panel) {
        contentPanel.add(panel, "DOCTORS");
    }
}
