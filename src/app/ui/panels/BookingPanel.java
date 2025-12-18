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

public class BookingPanel extends JPanel {

    private JComboBox<String> doctorBox;
    private List<Doctor> doctorList;
    private JTextField dateField;
    private JComboBox<String> timeBox;
    private JComboBox<String> paymentBox;

    public BookingPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);

        // Header
        JLabel title = new JLabel("Book an Appointment");
        title.setFont(Theme.HEADER_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 20));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Theme.BACKGROUND_COLOR);

        // Doctor Selection
        formPanel.add(new JLabel("Select Doctor:"));
        doctorBox = new JComboBox<>();
        loadDoctors();
        formPanel.add(doctorBox);

        // Date Selection
        formPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        dateField = new JTextField(LocalDate.now().plusDays(1).toString());
        formPanel.add(dateField);

        // Time Selection
        formPanel.add(new JLabel("Time Slot:"));
        String[] times = { "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00" };
        timeBox = new JComboBox<>(times);
        formPanel.add(timeBox);

        // Payment Method
        formPanel.add(new JLabel("Payment Method:"));
        String[] methods = { "Cash", "Credit Card", "PayPal" };
        paymentBox = new JComboBox<>(methods);
        formPanel.add(paymentBox);

        add(formPanel, BorderLayout.CENTER);

        // Action Button
        JButton bookBtn = new JButton("Confirm Booking");
        bookBtn.setFont(Theme.BUTTON_FONT);
        bookBtn.setBackground(Theme.PRIMARY_COLOR);
        bookBtn.setForeground(Theme.WHITE);
        bookBtn.addActionListener(e -> attemptBooking());

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(Theme.BACKGROUND_COLOR);
        btnPanel.add(bookBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadDoctors() {
        doctorBox.removeAllItems();
        doctorList = DoctorService.getInstance().getAllDoctors();
        for (Doctor d : doctorList) {
            doctorBox.addItem(d.getName() + " (" + d.getSpecialty() + ") - $" + d.getConsultationFee());
        }
    }

    private void attemptBooking() {
        int docIndex = doctorBox.getSelectedIndex();
        if (docIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor.");
            return;
        }
        Doctor selectedDoc = doctorList.get(docIndex);

        String dateStr = dateField.getText();
        String timeStr = (String) timeBox.getSelectedItem();

        LocalDateTime bookingDateTime;
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalTime time = LocalTime.parse(timeStr);
            bookingDateTime = LocalDateTime.of(date, time);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date Format! Use yyyy-MM-dd");
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
            // Logic to refund could go here
        }
    }
}
