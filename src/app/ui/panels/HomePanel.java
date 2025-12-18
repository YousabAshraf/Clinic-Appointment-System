package app.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
//import java.util.List;
import java.util.Optional;

import app.models.Appointment;
import app.models.User;
import app.security.SessionManager;
import app.services.appointment.AppointmentScheduler;
import app.ui.style.Theme;

public class HomePanel extends JPanel {

    private Runnable navigateToBooking;

    public HomePanel() {
        this(null);
    }

    public HomePanel(Runnable navigateToBooking) {
        this.navigateToBooking = navigateToBooking;
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);

        User user = SessionManager.getInstance().getLoggedUser();
        String role = user.getRole();

        if ("PATIENT".equalsIgnoreCase(role)) {
            buildPatientDashboard(user);
        } else {
            buildStandardDashboard(user, role);
        }
    }

    private void buildStandardDashboard(User user, String role) {
        // 1. Welcome Message
        JLabel welcomeLbl = new JLabel("Welcome back, " + user.getName() + "!", JLabel.CENTER);
        welcomeLbl.setFont(Theme.TITLE_FONT);
        welcomeLbl.setForeground(Theme.PRIMARY_COLOR);
        welcomeLbl.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(welcomeLbl, BorderLayout.NORTH);

        // 2. Role-Based Illustration
        String imagePath = "src/resources/images/admin_dashboard_bg.png";
        if ("DOCTOR".equalsIgnoreCase(role)) {
            imagePath = "src/resources/images/doctor_dashboard_bg.png";
        }

        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setBackground(Theme.BACKGROUND_COLOR);

        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(800, 450, Image.SCALE_SMOOTH);
        imagePanel.add(new JLabel(new ImageIcon(img)));
        add(imagePanel, BorderLayout.CENTER);
    }

    private void buildPatientDashboard(User patient) {
        // Main Container using GridBag for flexible layout
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(Theme.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Left Side: Hero Section (Text + Image) ---
        JPanel heroPanel = new JPanel();
        heroPanel.setLayout(new BoxLayout(heroPanel, BoxLayout.Y_AXIS));
        heroPanel.setBackground(Theme.BACKGROUND_COLOR);
        heroPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));

        JLabel welcomeLbl = new JLabel("<html>Good Morning,<br>" + patient.getName() + "</html>");
        welcomeLbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLbl.setForeground(Theme.PRIMARY_COLOR);
        welcomeLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel("<html>How are you feeling today?<br>Track your health journey with us.</html>");
        subLbl.setFont(Theme.REGULAR_FONT);
        subLbl.setForeground(Theme.TEXT_SECONDARY);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Access "Book Now" action
        JButton bookBtn = new JButton("Book New Appointment");
        Theme.styleButton(bookBtn, true);
        bookBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (navigateToBooking != null) {
            bookBtn.addActionListener(e -> navigateToBooking.run());
        }

        heroPanel.add(welcomeLbl);
        heroPanel.add(Box.createVerticalStrut(10));
        heroPanel.add(subLbl);
        heroPanel.add(Box.createVerticalStrut(30));
        heroPanel.add(bookBtn);

        // Illustration
        ImageIcon icon = new ImageIcon("src/resources/images/patient_dashboard_bg.png");
        Image img = icon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
        JLabel imgLabel = new JLabel(new ImageIcon(img));
        imgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        heroPanel.add(Box.createVerticalStrut(30));
        heroPanel.add(imgLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainContainer.add(heroPanel, gbc);

        // --- Right Side: Widgets ---
        JPanel widgetPanel = new JPanel(new GridBagLayout());
        widgetPanel.setBackground(Theme.BACKGROUND_COLOR);
        GridBagConstraints wGbc = new GridBagConstraints();

        // Next Appointment Widget
        JPanel nextApptCard = createNextAppointmentCard(patient);

        wGbc.gridx = 0;
        wGbc.gridy = 0;
        wGbc.weightx = 1.0;
        wGbc.fill = GridBagConstraints.HORIZONTAL;
        wGbc.insets = new Insets(40, 20, 0, 40);
        wGbc.anchor = GridBagConstraints.NORTH;
        widgetPanel.add(nextApptCard, wGbc);

        // Quick Tips or Info (Optional Placeholder)
        // ...

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        mainContainer.add(widgetPanel, gbc);

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createNextAppointmentCard(User patient) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Header
        JLabel header = new JLabel("Next Appointment");
        header.setFont(Theme.SUBHEADER_FONT);
        header.setForeground(Theme.TEXT_SECONDARY);
        card.add(header, BorderLayout.NORTH);

        // Content
        Optional<Appointment> nextAppt = AppointmentScheduler.getInstance().getAppointments().stream()
                .filter(a -> a.getPatient().getId() == patient.getId())
                .filter(a -> a.getDateTime().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Appointment::getDateTime))
                .findFirst();

        JPanel content = new JPanel(new GridLayout(0, 1, 5, 5));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        if (nextAppt.isPresent()) {
            Appointment a = nextAppt.get();
            DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("EEEE, MMMM d");
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("h:mm a");

            JLabel dateLbl = new JLabel(a.getDateTime().format(dayFmt));
            dateLbl.setFont(Theme.TITLE_FONT);
            dateLbl.setForeground(Theme.PRIMARY_COLOR);

            JLabel timeLbl = new JLabel(a.getDateTime().format(timeFmt));
            timeLbl.setFont(Theme.HEADER_FONT);
            timeLbl.setForeground(Theme.TEXT_PRIMARY);

            JLabel docLbl = new JLabel("with Dr. " + a.getDoctor().getName());
            docLbl.setFont(Theme.REGULAR_FONT);
            docLbl.setForeground(Theme.TEXT_SECONDARY);

            content.add(dateLbl);
            content.add(timeLbl);
            content.add(docLbl);
        } else {
            JLabel emptyLbl = new JLabel("<html>No upcoming appointments.<br>Book one today!</html>");
            emptyLbl.setFont(Theme.HEADER_FONT);
            emptyLbl.setForeground(Theme.TEXT_SECONDARY);
            content.add(emptyLbl);
        }

        card.add(content, BorderLayout.CENTER);
        return card;
    }
}
