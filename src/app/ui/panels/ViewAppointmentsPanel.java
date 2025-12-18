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

    private JTable table;
    private DefaultTableModel model;

    public ViewAppointmentsPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);

        // Header
        JLabel title = new JLabel("Appointments");
        title.setFont(Theme.HEADER_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Doctor", "Patient", "Date & Time", "Status", "Notes" };
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setFont(Theme.REGULAR_FONT);
        table.setRowHeight(25);
        table.getTableHeader().setFont(Theme.BUTTON_FONT);
        table.getTableHeader().setBackground(Theme.SECONDARY_COLOR);
        table.getTableHeader().setForeground(Theme.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Refresh Button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(Theme.BUTTON_FONT);
        refreshBtn.setBackground(Theme.PRIMARY_COLOR);
        refreshBtn.setForeground(Theme.WHITE);
        refreshBtn.addActionListener(e -> refreshData());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Theme.BACKGROUND_COLOR);
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Initial Load
        refreshData();
    }

    public void refreshData() {
        model.setRowCount(0); // Clear existing
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

        for (Appointment a : filtered) {
            model.addRow(new Object[] {
                    a.getId(),
                    a.getDoctor().getName(),
                    a.getPatient().getName(),
                    a.getDateTime().toString().replace("T", " "),
                    a.getStatus(),
                    a.getNotes()
            });
        }
    }
}
