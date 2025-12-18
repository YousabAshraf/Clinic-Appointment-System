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

        String nameLabel = "Dr. " + a.getDoctor().getName();
        String subLabel = a.getDoctor().getSpecialty();

        // If I am a doctor, show Patient Name instead
        User currentUser = SessionManager.getInstance().getLoggedUser();
        if ("DOCTOR".equalsIgnoreCase(currentUser.getRole())) {
            nameLabel = "Patient: " + a.getPatient().getName();
            subLabel = "ID: " + a.getPatient().getId();
        }

        JLabel mainName = new JLabel(nameLabel);
        mainName.setFont(Theme.SUBHEADER_FONT);
        mainName.setForeground(Theme.TEXT_PRIMARY);

        JLabel subMeta = new JLabel(subLabel);
        subMeta.setFont(Theme.REGULAR_FONT);
        subMeta.setForeground(Theme.TEXT_SECONDARY);

        infoPanel.add(mainName);
        infoPanel.add(subMeta);
        card.add(infoPanel, BorderLayout.CENTER);

        // Right: Status & Actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE);

        // Actions for Doctor if Pending
        if ("DOCTOR".equalsIgnoreCase(currentUser.getRole()) && "Pending".equalsIgnoreCase(a.getStatus())) {
            JButton approveBtn = new JButton("✔");
            approveBtn.setForeground(Theme.SUCCESS);
            approveBtn.setBackground(Color.WHITE);
            approveBtn.setBorder(BorderFactory.createLineBorder(Theme.SUCCESS));
            approveBtn.setToolTipText("Approve Appointment");
            approveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            approveBtn.setPreferredSize(new Dimension(30, 30));
            approveBtn.addActionListener(e -> {
                a.approve();
                refreshData();
            });

            JButton cancelBtn = new JButton("✖");
            cancelBtn.setForeground(Theme.ERROR_COLOR);
            cancelBtn.setBackground(Color.WHITE);
            cancelBtn.setBorder(BorderFactory.createLineBorder(Theme.ERROR_COLOR));
            cancelBtn.setToolTipText("Reject Appointment");
            cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cancelBtn.setPreferredSize(new Dimension(30, 30));
            cancelBtn.addActionListener(e -> {
                a.cancel();
                refreshData();
            });

            rightPanel.add(approveBtn);
            rightPanel.add(Box.createHorizontalStrut(5));
            rightPanel.add(cancelBtn);
            rightPanel.add(Box.createHorizontalStrut(10));
        }

        // Status Badge
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

        rightPanel.add(statusLbl);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }
}
