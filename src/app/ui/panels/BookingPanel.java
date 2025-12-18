package app.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import app.models.*;
import app.services.DoctorService;
import app.services.appointment.AppointmentScheduler;
import app.services.payments.*;
import app.security.SessionManager;
import app.ui.style.Theme;
import java.util.HashSet;
import java.util.Set;

public class BookingPanel extends JPanel {

    private JComboBox<String> doctorBox;
    private List<Doctor> doctorList;
    private JComboBox<String> dateBox;
    private JComboBox<String> timeBox;
    private JComboBox<String> paymentBox;
    private JLabel feeLabel;

    public BookingPanel() {
        setLayout(new GridBagLayout()); // Center the card
        setBackground(Theme.BACKGROUND_COLOR);

        // --- Card Panel ---
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));

        // Title
        JLabel title = new JLabel("Book Appointment");
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.PRIMARY_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);

        card.add(Box.createVerticalStrut(20));

        // --- Form Section (GridBag) ---
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Doctor
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(createLabel("Select Doctor"), gbc);

        gbc.gridx = 1;
        doctorBox = createComboBox();
        doctorBox.addActionListener(e -> loadAvailableDates()); // Also updates fee
        form.add(doctorBox, gbc);

        // Row 1: Fee Display
        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(createLabel("Consultation Fee"), gbc);

        gbc.gridx = 1;
        feeLabel = new JLabel("$0.00");
        feeLabel.setFont(Theme.HEADER_FONT);
        feeLabel.setForeground(Theme.ACCENT_COLOR);
        form.add(feeLabel, gbc);

        // Row 2: Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(createLabel("Select Date"), gbc);

        gbc.gridx = 1;
        dateBox = createComboBox();
        dateBox.addActionListener(e -> loadAvailableSlots());
        form.add(dateBox, gbc);

        // Row 3: Time
        gbc.gridx = 0;
        gbc.gridy = 3;
        form.add(createLabel("Available Time"), gbc);

        gbc.gridx = 1;
        timeBox = createComboBox();
        form.add(timeBox, gbc);

        // Row 4: Payment
        gbc.gridx = 0;
        gbc.gridy = 4;
        form.add(createLabel("Payment Method"), gbc);

        gbc.gridx = 1;
        String[] methods = { "Cash", "Credit Card", "PayPal" };
        paymentBox = createComboBox();
        for (String m : methods)
            paymentBox.addItem(m);
        form.add(paymentBox, gbc);

        card.add(form);
        card.add(Box.createVerticalStrut(30));

        // Only add card to main panel
        add(card);

        // Action Button
        JButton bookBtn = new JButton("CONFIRM BOOKING");
        Theme.styleButton(bookBtn, true);
        bookBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookBtn.setMaximumSize(new Dimension(200, 40));
        bookBtn.addActionListener(e -> attemptBooking());

        card.add(bookBtn);

        // Initialize
        loadDoctors();
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.SUBHEADER_FONT);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        return lbl;
    }

    private JComboBox<String> createComboBox() {
        JComboBox<String> box = new JComboBox<>();
        box.setFont(Theme.REGULAR_FONT);
        box.setBackground(Color.WHITE);
        box.setPreferredSize(new Dimension(250, 35));
        return box;
    }

    private void loadDoctors() {
        doctorBox.removeAllItems(); // Triggers listener -> loadAvailableDates -> updateFee
        doctorList = DoctorService.getInstance().getAllDoctors();
        for (Doctor d : doctorList) {
            doctorBox.addItem(d.getName() + " (" + d.getSpecialty() + ")");
        }
        if (doctorBox.getItemCount() > 0) {
            doctorBox.setSelectedIndex(0);
        }
    }

    private void loadAvailableDates() {
        dateBox.removeAllItems();
        int docIndex = doctorBox.getSelectedIndex();
        if (docIndex < 0 || doctorList == null || docIndex >= doctorList.size()) {
            return;
        }

        // Update Fee Display
        Doctor doc = doctorList.get(docIndex);
        feeLabel.setText(String.format("$%.2f", doc.getConsultationFee()));

        List<String> availability = doc.getAvailability();

        // Find which days of week the doctor works
        Set<String> workingDays = new HashSet<>();
        for (String slot : availability) {
            String day = slot.split(" ")[0]; // "Monday"
            workingDays.add(day);
        }

        // Generate next 14 days and check if they match working days and have at least
        // one slot free
        LocalDate today = LocalDate.now().plusDays(1); // Start from tomorrow
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < 14; i++) {
            LocalDate d = today.plusDays(i);
            String dayName = d.getDayOfWeek().toString(); // "MONDAY"
            dayName = dayName.charAt(0) + dayName.substring(1).toLowerCase(); // "Monday"

            if (workingDays.contains(dayName)) {
                // Check if FULLY booked
                boolean hasFreeSlot = false;
                for (String slot : availability) {
                    if (slot.startsWith(dayName)) {
                        String[] parts = slot.split(" ");
                        if (parts.length >= 2) {
                            LocalTime t = LocalTime.parse(parts[1]);
                            LocalDateTime dt = LocalDateTime.of(d, t);
                            if (!AppointmentScheduler.getInstance().isSlotBooked(doc.getId(), dt)) {
                                hasFreeSlot = true;
                                break;
                            }
                        }
                    }
                }

                if (hasFreeSlot) {
                    dateBox.addItem(d.format(formatter) + " (" + dayName + ")");
                }
            }
        }
    }

    private void loadAvailableSlots() {
        timeBox.removeAllItems();
        int docIndex = doctorBox.getSelectedIndex();
        if (docIndex < 0)
            return;

        if (dateBox.getSelectedItem() == null)
            return;

        String dateSelection = (String) dateBox.getSelectedItem(); // "2025-12-20 (Saturday)"
        String dateStr = dateSelection.split(" ")[0]; // "2025-12-20"

        LocalDate date = LocalDate.parse(dateStr);

        // Get Doctor and Day of Week
        Doctor doc = doctorList.get(docIndex);
        String dayOfWeek = date.getDayOfWeek().toString(); // "SATURDAY"
        dayOfWeek = dayOfWeek.charAt(0) + dayOfWeek.substring(1).toLowerCase(); // "Saturday"

        List<String> doctorAvail = doc.getAvailability();

        for (String slot : doctorAvail) {
            // Check if slot starts with the day (e.g., "Monday 09:00")
            if (slot.startsWith(dayOfWeek)) {
                String[] parts = slot.split(" ");
                if (parts.length >= 2) {
                    String timeStr = parts[1]; // "09:00"

                    // Check if already booked
                    LocalTime t = LocalTime.parse(timeStr);
                    LocalDateTime dt = LocalDateTime.of(date, t);

                    if (!AppointmentScheduler.getInstance().isSlotBooked(doc.getId(), dt)) {
                        timeBox.addItem(timeStr);
                    }
                }
            }
        }
    }

    private void attemptBooking() {
        int docIndex = doctorBox.getSelectedIndex();
        if (docIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor.");
            return;
        }

        if (dateBox.getSelectedItem() == null || timeBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a Date and Time.");
            return;
        }

        Doctor selectedDoc = doctorList.get(docIndex);

        String dateSelection = (String) dateBox.getSelectedItem(); // "2025-12-20 (Saturday)"
        String dateStr = dateSelection.split(" ")[0];
        String timeStr = (String) timeBox.getSelectedItem();

        LocalDateTime bookingDateTime;
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalTime time = LocalTime.parse(timeStr);
            bookingDateTime = LocalDateTime.of(date, time);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Error processing date/time.");
            return;
        }

        // 1. Process Payment
        String method = (String) paymentBox.getSelectedItem();
        PaymentStrategy strategy;
        switch (method) {
            case "Credit Card":
                strategy = new CreditCardPayment("1234567890123456", "John Doe", 12, 2030, "123");
                break;
            case "PayPal":
                strategy = new PayPalPayment("john@example.com", "password123");
                break;
            default:
                strategy = new CashPayment();
                break;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Confirm booking with " + selectedDoc.getName() + " for $" + selectedDoc.getConsultationFee() + "?",
                "Confirm Apppointment", JOptionPane.YES_NO_OPTION);

        if (result != JOptionPane.YES_OPTION)
            return;

        System.out.println("Processing payment...");
        strategy.pay(selectedDoc.getConsultationFee());

        // 2. Book
        Patient currentPatient = (Patient) SessionManager.getInstance().getLoggedUser();
        boolean success = AppointmentScheduler.getInstance().bookAppointment(selectedDoc, currentPatient,
                bookingDateTime);

        if (success) {
            JOptionPane.showMessageDialog(this, "Booking Successful!\nPaid via " + method);
        } else {
            JOptionPane.showMessageDialog(this, "Booking Failed! Slot already taken or error.");
        }
    }
}
