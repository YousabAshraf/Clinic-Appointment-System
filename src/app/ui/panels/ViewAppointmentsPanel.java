package app.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import app.models.*;
import app.services.appointment.AppointmentScheduler;
import app.ui.style.Theme;
import app.security.SessionManager;

public class ViewAppointmentsPanel extends JPanel {

    private JPanel listPanel;
    private JScrollPane scrollPane;
    private String currentView = "ALL"; // ALL, TODAY, WEEK

    public ViewAppointmentsPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Panel: Header + Toolbar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Theme.BACKGROUND_COLOR);

        JLabel title = new JLabel("Appointments");
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.PRIMARY_DARK);
        topPanel.add(title, BorderLayout.WEST);

        // Toolbar for Filters & Actions
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setBackground(Theme.BACKGROUND_COLOR);

        // Filters
        JButton allBtn = createFilterBtn("All", "ALL");
        JButton todayBtn = createFilterBtn("Today", "TODAY");
        JButton weekBtn = createFilterBtn("This Week", "WEEK");

        toolbar.add(allBtn);
        toolbar.add(todayBtn);
        toolbar.add(weekBtn);

        // Doctor Actions
        User user = SessionManager.getInstance().getLoggedUser();
        if ("DOCTOR".equals(user.getRole())) {
            toolbar.add(Box.createHorizontalStrut(20));
            JButton blockBtn = new JButton("Block Leave");
            Theme.styleButton(blockBtn, false);
            blockBtn.addActionListener(e -> showBlockLeaveDialog());
            toolbar.add(blockBtn);
        }

        topPanel.add(toolbar, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

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

    private JButton createFilterBtn(String label, String code) {
        JButton btn = new JButton(label);
        btn.setFont(Theme.BUTTON_FONT);
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Theme.PRIMARY_COLOR);
        btn.setBorder(BorderFactory.createLineBorder(Theme.PRIMARY_COLOR));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 25));

        btn.addActionListener(e -> {
            this.currentView = code;
            refreshData();
        });
        return btn;
    }

    private void showBlockLeaveDialog() {
        // Simple Input for Date (YYYY-MM-DD) and Time (HH:MM)
        // For simplicity, using multiple option dialogs or a small form

        JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField dateFld = new JTextField(LocalDate.now().plusDays(1).toString());
        String[] slots = { "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00" };
        JComboBox<String> timeBox = new JComboBox<>(slots);

        p.add(new JLabel("Date (YYYY-MM-DD):"));
        p.add(dateFld);
        p.add(new JLabel("Time:"));
        p.add(timeBox);

        int res = JOptionPane.showConfirmDialog(this, p, "Block Slot / Leave", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                LocalDate d = LocalDate.parse(dateFld.getText());
                LocalTime t = LocalTime.parse((String) timeBox.getSelectedItem());
                LocalDateTime dt = LocalDateTime.of(d, t);

                Doctor doc = (Doctor) SessionManager.getInstance().getLoggedUser(); // Casting assumes calling logic is
                                                                                    // correct
                boolean success = AppointmentScheduler.getInstance().blockSlot(doc, dt);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Slot Blocked Successfully.");
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed. Slot might be taken.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format.");
            }
        }
    }

    public void refreshData() {
        listPanel.removeAll();

        User currentUser = SessionManager.getInstance().getLoggedUser();
        List<Appointment> all = AppointmentScheduler.getInstance().getAppointments();

        // 1. Role Filter
        List<Appointment> roleFiltered;
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            roleFiltered = all;
        } else if ("DOCTOR".equalsIgnoreCase(currentUser.getRole())) {
            roleFiltered = all.stream()
                    .filter(a -> a.getDoctor().getId() == currentUser.getId())
                    .collect(Collectors.toList());
        } else { // PATIENT
            roleFiltered = all.stream()
                    .filter(a -> a.getPatient().getId() == currentUser.getId())
                    .collect(Collectors.toList());
        }

        // 2. View Filter (Date)
        LocalDate today = LocalDate.now();
        List<Appointment> finalFiltered;

        if ("TODAY".equals(currentView)) {
            finalFiltered = roleFiltered.stream()
                    .filter(a -> a.getDateTime().toLocalDate().equals(today))
                    .collect(Collectors.toList());
        } else if ("WEEK".equals(currentView)) {
            finalFiltered = roleFiltered.stream()
                    .filter(a -> {
                        LocalDate d = a.getDateTime().toLocalDate();
                        return !d.isBefore(today) && d.isBefore(today.plusDays(7));
                    })
                    .collect(Collectors.toList());
        } else {
            finalFiltered = roleFiltered;
        }

        if (finalFiltered.isEmpty()) {
            JLabel empty = new JLabel("No appointments found.");
            empty.setFont(Theme.HEADER_FONT);
            empty.setForeground(Theme.TEXT_SECONDARY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalStrut(50));
            listPanel.add(empty);
        } else {
            for (Appointment a : finalFiltered) {
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

        boolean isBlocked = (a.getPatient().getId() == -1);

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

        User currentUser = SessionManager.getInstance().getLoggedUser();
        // If I am a doctor, show Patient Name instead
        if ("DOCTOR".equalsIgnoreCase(currentUser.getRole())) {
            if (isBlocked) {
                nameLabel = "BLOCKED SLOT (LEAVE)";
                subLabel = "Unavailable for booking";
                card.setBackground(new Color(245, 245, 245)); // Grey out
                infoPanel.setBackground(new Color(245, 245, 245));
                datePanel.setBackground(new Color(245, 245, 245));
            } else {
                nameLabel = "Patient: " + a.getPatient().getName();
                subLabel = "ID: " + a.getPatient().getId();
            }
        }

        JLabel mainName = new JLabel(nameLabel);
        mainName.setFont(Theme.SUBHEADER_FONT);
        mainName.setForeground(isBlocked ? Theme.TEXT_SECONDARY : Theme.TEXT_PRIMARY);

        JLabel subMeta = new JLabel(subLabel);
        subMeta.setFont(Theme.REGULAR_FONT);
        subMeta.setForeground(Theme.TEXT_SECONDARY);

        infoPanel.add(mainName);
        infoPanel.add(subMeta);
        card.add(infoPanel, BorderLayout.CENTER);

        // Right: Status & Actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(isBlocked ? new Color(245, 245, 245) : Color.WHITE);

        if (isBlocked) {
            JButton unblockBtn = new JButton("Unblock");
            unblockBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            unblockBtn.setForeground(Theme.ERROR_COLOR);
            unblockBtn.setBackground(Color.WHITE);
            unblockBtn.setBorder(BorderFactory.createLineBorder(Theme.ERROR_COLOR));
            unblockBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            unblockBtn.setPreferredSize(new Dimension(80, 25));
            unblockBtn.addActionListener(e -> {
                a.cancel();
                refreshData();
            });
            rightPanel.add(unblockBtn);

            JLabel statusLbl = new JLabel(" BLOCKED ");
            statusLbl.setFont(Theme.BUTTON_FONT);
            statusLbl.setOpaque(true);
            statusLbl.setBackground(Color.GRAY);
            statusLbl.setForeground(Color.WHITE);
            statusLbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            rightPanel.add(statusLbl);

        } else {
            if (!"Cancelled".equalsIgnoreCase(a.getStatus()) && !"Rejected".equalsIgnoreCase(a.getStatus())) {
                JButton reschedBtn = new JButton("📅");
                reschedBtn.setToolTipText("Reschedule");
                reschedBtn.setBorder(null);
                reschedBtn.setBackground(Color.WHITE);
                reschedBtn.setPreferredSize(new Dimension(30, 30));
                reschedBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                reschedBtn.addActionListener(e -> showRescheduleDialog(a));
                rightPanel.add(reschedBtn);
            }

            if ("DOCTOR".equalsIgnoreCase(currentUser.getRole()) && "Pending".equalsIgnoreCase(a.getStatus())) {
                JButton approveBtn = new JButton("✔");
                approveBtn.setForeground(Theme.SUCCESS);
                approveBtn.setBackground(Color.WHITE);
                approveBtn.setBorder(BorderFactory.createLineBorder(Theme.SUCCESS));
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
                cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                cancelBtn.setPreferredSize(new Dimension(30, 30));
                cancelBtn.addActionListener(e -> {
                    a.cancel();
                    refreshData();
                });

                rightPanel.add(Box.createHorizontalStrut(10));
                rightPanel.add(approveBtn);
                rightPanel.add(Box.createHorizontalStrut(5));
                rightPanel.add(cancelBtn);
                rightPanel.add(Box.createHorizontalStrut(10));
            }

            JLabel statusLbl = new JLabel(" " + a.getStatus() + " ");
            statusLbl.setFont(Theme.BUTTON_FONT);
            statusLbl.setOpaque(true);
            statusLbl.setForeground(Color.WHITE);

            if ("Confirmed".equalsIgnoreCase(a.getStatus())) {
                statusLbl.setBackground(Theme.SUCCESS);
            } else if ("Cancelled".equalsIgnoreCase(a.getStatus()) || "Rejected".equalsIgnoreCase(a.getStatus())) {
                statusLbl.setBackground(Theme.ERROR_COLOR);
            } else {
                statusLbl.setBackground(Theme.WARNING);
            }
            statusLbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Pill padding

            rightPanel.add(statusLbl);
        }

        card.add(rightPanel, BorderLayout.EAST);
        return card;
    }

    private void showRescheduleDialog(Appointment a) {
        // Validation: Can only reschedule execution if we verify slot
        JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField dateFld = new JTextField(a.getDateTime().toLocalDate().toString());
        String[] slots = { "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00" };
        JComboBox<String> timeBox = new JComboBox<>(slots);
        timeBox.setSelectedItem(a.getDateTime().toLocalTime().toString());

        p.add(new JLabel("New Date (YYYY-MM-DD):"));
        p.add(dateFld);
        p.add(new JLabel("New Time:"));
        p.add(timeBox);

        int res = JOptionPane.showConfirmDialog(this, p, "Reschedule Appointment", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                LocalDate d = LocalDate.parse(dateFld.getText());
                LocalTime t = LocalTime.parse((String) timeBox.getSelectedItem());
                LocalDateTime dt = LocalDateTime.of(d, t);

                // Check if slot available
                if (AppointmentScheduler.getInstance().isSlotBooked(a.getDoctor().getId(), dt)) {
                    JOptionPane.showMessageDialog(this, "Slot already booked! Choose another.");
                    return;
                }

                a.reschedule(dt);
                JOptionPane.showMessageDialog(this, "Rescheduled Successfully.");
                refreshData();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date.");
            }
        }
    }
}
