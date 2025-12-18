package app.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import app.models.*;
import app.services.appointment.AppointmentScheduler;
import app.ui.style.Theme;
import app.security.SessionManager;

public class ViewAppointmentsPanel extends JPanel {

    private JPanel listPanel;
    private JScrollPane scrollPane;

    public ViewAppointmentsPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel title = new JLabel("My Appointments");
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.PRIMARY_DARK);
        add(title, BorderLayout.NORTH);

        // List Container
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Theme.BACKGROUND_COLOR);

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Theme.BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Refresh Button
        JButton refreshBtn = new JButton("Refresh Data");
        Theme.styleButton(refreshBtn, true);
        refreshBtn.addActionListener(e -> refreshData());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Theme.BACKGROUND_COLOR);
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Initial Load
        refreshData();
    }

    public void refreshData() {
        listPanel.removeAll();

        User currentUser = SessionManager.getInstance().getLoggedUser();
        List<Appointment> all = AppointmentScheduler.getInstance().getAppointments();
        List<Appointment> filtered;

        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            filtered = all;
        } else if ("DOCTOR".equalsIgnoreCase(currentUser.getRole())) {
            filtered = all.stream()
                    .filter(a -> a.getDoctor().getId() == currentUser.getId())
                    .collect(Collectors.toList());
        } else { // PATIENT
            filtered = all.stream()
                    .filter(a -> a.getPatient().getId() == currentUser.getId())
                    .collect(Collectors.toList());
        }

        if (filtered.isEmpty()) {
            JLabel empty = new JLabel("No appointments found.");
            empty.setFont(Theme.HEADER_FONT);
            empty.setForeground(Theme.TEXT_SECONDARY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalStrut(50));
            listPanel.add(empty);
        } else {
            for (Appointment a : filtered) {
                listPanel.add(createAppointmentCard(a));
                listPanel.add(Box.createVerticalStrut(15)); // Gap between cards
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createAppointmentCard(Appointment a) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        card.setMaximumSize(new Dimension(800, 100));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Left: Date
        JPanel datePanel = new JPanel(new GridLayout(2, 1));
        datePanel.setBackground(Color.WHITE);
        datePanel.setPreferredSize(new Dimension(100, 60));

        String dateStr = a.getDateTime().toLocalDate().toString(); // 2025-12-20
        String timeStr = a.getDateTime().toLocalTime().toString(); // 09:00

        JLabel dateLbl = new JLabel(dateStr);
        dateLbl.setFont(Theme.HEADER_FONT);
        dateLbl.setForeground(Theme.PRIMARY_COLOR);

        JLabel timeLbl = new JLabel(timeStr);
        timeLbl.setFont(Theme.SUBHEADER_FONT);
        timeLbl.setForeground(Theme.TEXT_SECONDARY);

        datePanel.add(dateLbl);
        datePanel.add(timeLbl);
        card.add(datePanel, BorderLayout.WEST);

        // Center: Info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(Color.WHITE);

        JLabel docName = new JLabel(a.getDoctor().getName());
        docName.setFont(Theme.SUBHEADER_FONT);
        docName.setForeground(Theme.TEXT_PRIMARY);

        JLabel specialty = new JLabel(a.getDoctor().getSpecialty());
        specialty.setFont(Theme.REGULAR_FONT);
        specialty.setForeground(Theme.TEXT_SECONDARY);

        infoPanel.add(docName);
        infoPanel.add(specialty);
        card.add(infoPanel, BorderLayout.CENTER);

        // Right: Status
        JLabel statusLbl = new JLabel(" " + a.getStatus() + " ");
        statusLbl.setFont(Theme.BUTTON_FONT);
        statusLbl.setOpaque(true);
        statusLbl.setForeground(Color.WHITE);

        if ("Confirmed".equalsIgnoreCase(a.getStatus())) {
            statusLbl.setBackground(Theme.SUCCESS);
        } else if ("Cancelled".equalsIgnoreCase(a.getStatus())) {
            statusLbl.setBackground(Theme.ERROR_COLOR);
        } else {
            statusLbl.setBackground(Theme.WARNING);
        }
        statusLbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Pill padding

        JPanel statusContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusContainer.setBackground(Color.WHITE);
        statusContainer.add(statusLbl);

        card.add(statusContainer, BorderLayout.EAST);

        return card;
    }
}
