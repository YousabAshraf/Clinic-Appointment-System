package app.ui;

import javax.swing.*;
import java.awt.*;
import app.security.SessionManager;
import app.models.User;
import app.ui.style.Theme;
import app.ui.panels.*;

public class Dashboard extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    public Dashboard() {
        setTitle("Clinic Appointment System - Dashboard");
        setSize(1100, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        User user = SessionManager.getInstance().getLoggedUser();
        if (user == null) {
            new LoginForm().setVisible(true);
            this.dispose();
            return;
        }
        String role = user.getRole();

        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(Theme.PRIMARY_COLOR);
        sidebar.setPreferredSize(new Dimension(260, getHeight()));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);

        JLabel brandLbl = new JLabel("CLINIC SYSTEM");
        brandLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandLbl.setForeground(Color.WHITE);
        brandLbl.setBorder(BorderFactory.createEmptyBorder(30, 25, 30, 20));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        sidebar.add(brandLbl, gbc);

        JPanel menuPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        menuPanel.setBackground(Theme.PRIMARY_COLOR);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        addNavButton(menuPanel, "Home", "HOME", "🏠");

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Theme.BACKGROUND_COLOR);

        if ("ADMIN".equals(role)) {
            addNavButton(menuPanel, "Doctors", "DOCTORS", "👨‍⚕️");
            addNavButton(menuPanel, "Appointments", "APPOINTMENTS", "📅");

            contentPanel.add(new DoctorManagementPanel(), "DOCTORS");
        } else if ("DOCTOR".equals(role)) {
            addNavButton(menuPanel, "Profile", "PROFILE", "👨‍⚕️");
            addNavButton(menuPanel, "My Schedule", "APPOINTMENTS", "📅");

            contentPanel.add(new DoctorProfilePanel(), "PROFILE");
        } else {
            addNavButton(menuPanel, "Book New", "BOOKING", "➕");
            addNavButton(menuPanel, "My Visits", "APPOINTMENTS", "📅");

            contentPanel.add(new BookingPanel(), "BOOKING");
        }

        contentPanel.add(new HomePanel(() -> cardLayout.show(contentPanel, "BOOKING")), "HOME");
        contentPanel.add(new ViewAppointmentsPanel(), "APPOINTMENTS");

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        sidebar.add(menuPanel, gbc);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(0, 0, 0, 30)); // Slight overlay
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel nameLbl = new JLabel(user.getName());
        nameLbl.setFont(Theme.SUBHEADER_FONT);
        nameLbl.setForeground(Color.WHITE);

        JLabel roleLbl = new JLabel(role);
        roleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLbl.setForeground(new Color(200, 200, 200));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(nameLbl);
        textPanel.add(roleLbl);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(255, 255, 255, 40));
        logoutBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setPreferredSize(new Dimension(80, 30));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            SessionManager.getInstance().logout();
            new LoginForm().setVisible(true);
            this.dispose();
        });

        footerPanel.add(textPanel, BorderLayout.CENTER);
        footerPanel.add(logoutBtn, BorderLayout.EAST);

        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.SOUTH;
        sidebar.add(footerPanel, gbc);

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "HOME");
    }

    private void addNavButton(JPanel panel, String label, String cardName, String icon) {
        JButton btn = new JButton(icon + "   " + label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Theme.PRIMARY_COLOR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(Theme.PRIMARY_DARK);
                btn.setOpaque(true);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Theme.PRIMARY_COLOR);
            }
        });

        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));

        panel.add(btn);
    }
}
