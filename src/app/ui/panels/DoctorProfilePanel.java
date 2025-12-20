package app.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import app.models.Doctor;
import app.services.DoctorService;
import app.security.SessionManager;
import app.ui.style.Theme;

public class DoctorProfilePanel extends JPanel {

    private Doctor doctor;

    public DoctorProfilePanel() {
        doctor = (Doctor) SessionManager.getInstance().getLoggedUser();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("My Profile");
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.PRIMARY_DARK);
        add(title);
        add(Box.createVerticalStrut(20));

        JTextField nameField = new JTextField(doctor.getName());
        nameField.setEditable(false);
        JTextField emailField = new JTextField(doctor.getEmail());
        emailField.setEditable(false);
        JTextField specField = new JTextField(doctor.getSpecialty());
        JTextField feeField = new JTextField(String.valueOf(doctor.getConsultationFee()));

        JButton saveBtn = new JButton("Update Profile");
        Theme.styleButton(saveBtn, true);
        saveBtn.addActionListener(e -> {
            try {
                double fee = Double.parseDouble(feeField.getText());
                DoctorService.getInstance().updateDoctor(doctor.getId(), specField.getText(), fee);
                JOptionPane.showMessageDialog(this, "Profile updated!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        });

        add(new JLabel("Name:")); add(nameField);
        add(new JLabel("Email:")); add(emailField);
        add(new JLabel("Specialty:")); add(specField);
        add(new JLabel("Fee:")); add(feeField);
        add(Box.createVerticalStrut(20));
        add(saveBtn);
        add(Box.createVerticalStrut(30));

        JLabel availTitle = new JLabel("Manage Availability");
        availTitle.setFont(Theme.HEADER_FONT);
        availTitle.setForeground(Theme.PRIMARY_DARK);
        add(availTitle);
        add(Box.createVerticalStrut(10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String s : doctor.getAvailability()) {
            listModel.addElement(s);
        }
        JList<String> list = new JList<>(listModel);
        list.setFont(Theme.REGULAR_FONT);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(400, 200));
        add(scroll);
        add(Box.createVerticalStrut(10));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(Theme.BACKGROUND_COLOR);

        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
        JComboBox<String> dayBox = new JComboBox<>(days);

        String[] hours = { "09", "10", "11", "12", "13", "14", "15", "16", "17" };
        JComboBox<String> hourBox = new JComboBox<>(hours);

        String[] mins = { "00", "15", "30", "45" };
        JComboBox<String> minBox = new JComboBox<>(mins);

        inputPanel.add(new JLabel("Day:"));
        inputPanel.add(dayBox);
        inputPanel.add(new JLabel("Time:"));
        inputPanel.add(hourBox);
        inputPanel.add(new JLabel(":"));
        inputPanel.add(minBox);
        add(inputPanel);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setBackground(Theme.BACKGROUND_COLOR);

        JButton addSlotBtn = new JButton("Add Slot");
        Theme.styleButton(addSlotBtn, true);
        JButton removeSlotBtn = new JButton("Remove Selected");
        Theme.styleButton(removeSlotBtn, false);
        removeSlotBtn.setForeground(Theme.ERROR_COLOR);

        JButton saveAvailBtn = new JButton("Save Availability");
        Theme.styleButton(saveAvailBtn, false);

        btnRow.add(addSlotBtn);
        btnRow.add(removeSlotBtn);
        btnRow.add(saveAvailBtn);
        add(btnRow);

        addSlotBtn.addActionListener(e -> {
            String day = (String) dayBox.getSelectedItem();
            String time = hourBox.getSelectedItem() + ":" + minBox.getSelectedItem();
            String slot = day + " " + time;

            if (!listModel.contains(slot)) {
                listModel.addElement(slot);
            } else {
                JOptionPane.showMessageDialog(this, "Slot already exists!");
            }
        });

        removeSlotBtn.addActionListener(e -> {
            if (!list.isSelectionEmpty()) {
                listModel.remove(list.getSelectedIndex());
            }
        });

        saveAvailBtn.addActionListener(e -> {
            List<String> newAvail = new ArrayList<>();
            for (int i = 0; i < listModel.size(); i++) {
                newAvail.add(listModel.get(i));
            }
            DoctorService.getInstance().updateDoctorAvailability(doctor.getId(), newAvail);
            doctor.setAvailability(newAvail);
            JOptionPane.showMessageDialog(this, "Availability saved!");
        });
    }
}
