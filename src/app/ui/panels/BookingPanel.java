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
    
    private JPanel paymentDetailsPanel;
    private JLabel cardNumberLabel;
    private JTextField cardNumberField;
    private JLabel cardHolderLabel;
    private JTextField cardHolderField;
    private JLabel expiryMonthLabel;
    private JTextField expiryMonthField;
    private JLabel expiryYearLabel;
    private JTextField expiryYearField;
    private JLabel cvvLabel;
    private JTextField cvvField;
    private JLabel paypalEmailLabel;
    private JTextField paypalEmailField;
    private JLabel paypalPasswordLabel;
    private JPasswordField paypalPasswordField;

    public BookingPanel() {
        setLayout(new GridBagLayout()); // Center the card
        setBackground(Theme.BACKGROUND_COLOR);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));

        JLabel title = new JLabel("Book Appointment");
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.PRIMARY_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);

        card.add(Box.createVerticalStrut(20));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(createLabel("Select Doctor"), gbc);

        gbc.gridx = 1;
        doctorBox = createComboBox();
        doctorBox.addActionListener(e -> loadAvailableDates()); // Also updates fee
        form.add(doctorBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(createLabel("Consultation Fee"), gbc);

        gbc.gridx = 1;
        feeLabel = new JLabel("$0.00");
        feeLabel.setFont(Theme.HEADER_FONT);
        feeLabel.setForeground(Theme.ACCENT_COLOR);
        form.add(feeLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(createLabel("Select Date"), gbc);

        gbc.gridx = 1;
        dateBox = createComboBox();
        dateBox.addActionListener(e -> loadAvailableSlots());
        form.add(dateBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        form.add(createLabel("Available Time"), gbc);

        gbc.gridx = 1;
        timeBox = createComboBox();
        form.add(timeBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        form.add(createLabel("Payment Method"), gbc);

        gbc.gridx = 1;
        String[] methods = { "Cash", "Credit Card", "PayPal" };
        paymentBox = createComboBox();
        for (String m : methods)
            paymentBox.addItem(m);
        paymentBox.addActionListener(e -> updatePaymentDetailsVisibility());
        form.add(paymentBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        paymentDetailsPanel = createPaymentDetailsPanel();
        paymentDetailsPanel.setVisible(false);
        form.add(paymentDetailsPanel, gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;

        card.add(form);
        card.add(Box.createVerticalStrut(30));

        add(card);

        JButton bookBtn = new JButton("CONFIRM BOOKING");
        Theme.styleButton(bookBtn, true);
        bookBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookBtn.setMaximumSize(new Dimension(200, 40));
        bookBtn.addActionListener(e -> attemptBooking());

        card.add(bookBtn);

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

    private JPanel createPaymentDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Payment Details",
                0, 0,
                Theme.SUBHEADER_FONT));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row++;
        cardNumberLabel = createLabel("Card Number:");
        panel.add(cardNumberLabel, gbc);
        gbc.gridx = 1;
        cardNumberField = createTextField();
        panel.add(cardNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        cardHolderLabel = createLabel("Cardholder Name:");
        panel.add(cardHolderLabel, gbc);
        gbc.gridx = 1;
        cardHolderField = createTextField();
        panel.add(cardHolderField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        expiryMonthLabel = createLabel("Expiry Month (MM):");
        panel.add(expiryMonthLabel, gbc);
        gbc.gridx = 1;
        expiryMonthField = createTextField();
        panel.add(expiryMonthField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        expiryYearLabel = createLabel("Expiry Year (YYYY):");
        panel.add(expiryYearLabel, gbc);
        gbc.gridx = 1;
        expiryYearField = createTextField();
        panel.add(expiryYearField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        cvvLabel = createLabel("CVV:");
        panel.add(cvvLabel, gbc);
        gbc.gridx = 1;
        cvvField = createTextField();
        panel.add(cvvField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        paypalEmailLabel = createLabel("PayPal Email:");
        panel.add(paypalEmailLabel, gbc);
        gbc.gridx = 1;
        paypalEmailField = createTextField();
        panel.add(paypalEmailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        paypalPasswordLabel = createLabel("PayPal Password:");
        panel.add(paypalPasswordLabel, gbc);
        gbc.gridx = 1;
        paypalPasswordField = new JPasswordField();
        paypalPasswordField.setFont(Theme.REGULAR_FONT);
        paypalPasswordField.setBackground(Color.WHITE);
        paypalPasswordField.setPreferredSize(new Dimension(350, 40));
        paypalPasswordField.setMinimumSize(new Dimension(300, 40));
        panel.add(paypalPasswordField, gbc);

        return panel;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(Theme.REGULAR_FONT);
        field.setBackground(Color.WHITE);
        field.setPreferredSize(new Dimension(350, 40));
        field.setMinimumSize(new Dimension(300, 40));
        return field;
    }

    private void updatePaymentDetailsVisibility() {
        String method = (String) paymentBox.getSelectedItem();
        if (method == null) {
            paymentDetailsPanel.setVisible(false);
            return;
        }

        boolean showDetails = !method.equals("Cash");
        paymentDetailsPanel.setVisible(showDetails);

        if (method.equals("Credit Card")) {
            cardNumberLabel.setVisible(true);
            cardNumberField.setVisible(true);
            cardHolderLabel.setVisible(true);
            cardHolderField.setVisible(true);
            expiryMonthLabel.setVisible(true);
            expiryMonthField.setVisible(true);
            expiryYearLabel.setVisible(true);
            expiryYearField.setVisible(true);
            cvvLabel.setVisible(true);
            cvvField.setVisible(true);
            paypalEmailLabel.setVisible(false);
            paypalEmailField.setVisible(false);
            paypalPasswordLabel.setVisible(false);
            paypalPasswordField.setVisible(false);
        } else if (method.equals("PayPal")) {
            cardNumberLabel.setVisible(false);
            cardNumberField.setVisible(false);
            cardHolderLabel.setVisible(false);
            cardHolderField.setVisible(false);
            expiryMonthLabel.setVisible(false);
            expiryMonthField.setVisible(false);
            expiryYearLabel.setVisible(false);
            expiryYearField.setVisible(false);
            cvvLabel.setVisible(false);
            cvvField.setVisible(false);
            paypalEmailLabel.setVisible(true);
            paypalEmailField.setVisible(true);
            paypalPasswordLabel.setVisible(true);
            paypalPasswordField.setVisible(true);
        }

        revalidate();
        repaint();
    }

    private void loadDoctors() {
        doctorBox.removeAllItems();
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

        Doctor doc = doctorList.get(docIndex);
        feeLabel.setText(String.format("$%.2f", doc.getConsultationFee()));

        List<String> availability = doc.getAvailability();

        Set<String> workingDays = new HashSet<>();
        for (String slot : availability) {
            String day = slot.split(" ")[0]; // "Monday"
            workingDays.add(day);
        }


        LocalDate today = LocalDate.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < 14; i++) {
            LocalDate d = today.plusDays(i);
            String dayName = d.getDayOfWeek().toString();
            dayName = dayName.charAt(0) + dayName.substring(1).toLowerCase();

            if (workingDays.contains(dayName)) {
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

        String dateSelection = (String) dateBox.getSelectedItem();
        String dateStr = dateSelection.split(" ")[0];

        LocalDate date = LocalDate.parse(dateStr);

        Doctor doc = doctorList.get(docIndex);
        String dayOfWeek = date.getDayOfWeek().toString();
        dayOfWeek = dayOfWeek.charAt(0) + dayOfWeek.substring(1).toLowerCase();

        List<String> doctorAvail = doc.getAvailability();

        for (String slot : doctorAvail) {
            if (slot.startsWith(dayOfWeek)) {
                String[] parts = slot.split(" ");
                if (parts.length >= 2) {
                    String timeStr = parts[1];

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

        String dateSelection = (String) dateBox.getSelectedItem();
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

        String method = (String) paymentBox.getSelectedItem();
        PaymentContext paymentContext = new PaymentContext();
        PaymentStrategy strategy = null;

        try {
            switch (method) {
                case "Credit Card":
                    String cardNumber = cardNumberField.getText().trim();
                    String cardHolder = cardHolderField.getText().trim();
                    String expiryMonthStr = expiryMonthField.getText().trim();
                    String expiryYearStr = expiryYearField.getText().trim();
                    String cvv = cvvField.getText().trim();

                    if (cardNumber.isEmpty() || cardHolder.isEmpty() || 
                        expiryMonthStr.isEmpty() || expiryYearStr.isEmpty() || cvv.isEmpty()) {
                        JOptionPane.showMessageDialog(this, 
                            "Please fill in all credit card details.", 
                            "Payment Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        int expiryMonth = Integer.parseInt(expiryMonthStr);
                        int expiryYear = Integer.parseInt(expiryYearStr);
                        strategy = new CreditCardPayment(cardNumber, cardHolder, expiryMonth, expiryYear, cvv);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, 
                            "Invalid expiry month or year. Please enter valid numbers.", 
                            "Payment Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;

                case "PayPal":
                    String email = paypalEmailField.getText().trim();
                    String password = new String(paypalPasswordField.getPassword());

                    if (email.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(this, 
                            "Please fill in PayPal email and password.", 
                            "Payment Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    strategy = new PayPalPayment(email, password);
                    break;

                default:
                    strategy = new CashPayment();
                    break;
            }

            paymentContext.setStrategy(strategy);

            int result = JOptionPane.showConfirmDialog(this,
                    "Confirm booking with " + selectedDoc.getName() + " for $" + 
                    String.format("%.2f", selectedDoc.getConsultationFee()) + "?",
                    "Confirm Appointment", JOptionPane.YES_NO_OPTION);

            if (result != JOptionPane.YES_OPTION)
                return;

            System.out.println("Processing payment...");
            paymentContext.executePayment(selectedDoc.getConsultationFee());

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, 
                "Payment Error: " + e.getMessage(), 
                "Payment Failed", 
                JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, 
                "Payment Error: " + e.getMessage(), 
                "Payment Failed", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Book
        Patient currentPatient = (Patient) SessionManager.getInstance().getLoggedUser();
        boolean success = AppointmentScheduler.getInstance().bookAppointment(selectedDoc, currentPatient,
                bookingDateTime);

        if (success) {
            String successMessage;
            if (method.equals("Cash")) {
                successMessage = "Booking Successful!\nAmount " + 
                    String.format("%.2f", selectedDoc.getConsultationFee()) + 
                    " will be collected in the clinic";
            } else {
                successMessage = "Booking Successful!\nPaid via " + method;
            }
            JOptionPane.showMessageDialog(this, successMessage);
        } else {
            JOptionPane.showMessageDialog(this, "Booking Failed! Slot already taken or error.");
        }
    }
}
