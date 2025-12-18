package app.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import app.models.Doctor;
import app.services.DoctorService;
import app.ui.style.Theme;

public class DoctorManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public DoctorManagementPanel() {
        setLayout(new BorderLayout(0, 20)); // Vertical gap
        setBackground(Theme.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel title = new JLabel("Manage Doctors");
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.PRIMARY_DARK);
        add(title, BorderLayout.NORTH);

        // Table
        String[] cols = { "ID", "Name", "Email", "Specialty", "Fee" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        Theme.styleTable(table); // Apply new styling

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Clean look
        scrollPane.getViewport().setBackground(Theme.SURFACE_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        // Controls
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setBackground(Theme.BACKGROUND_COLOR);

        JButton addBtn = new JButton("Add Doctor");
        JButton editBtn = new JButton("Edit Details");
        JButton availBtn = new JButton("Manage Availability");
        JButton delBtn = new JButton("Delete Doctor");

        // Style Buttons
        Theme.styleButton(addBtn, true);
        Theme.styleButton(editBtn, false);
        Theme.styleButton(availBtn, false);
        Theme.styleButton(delBtn, false);
        delBtn.setBackground(Theme.SURFACE_COLOR);
        delBtn.setForeground(Theme.ERROR_COLOR); // Red text for delete
        delBtn.setBorder(BorderFactory.createLineBorder(Theme.ERROR_COLOR));

        addBtn.addActionListener(e -> showAddDoctorDialog());
        editBtn.addActionListener(e -> showEditDoctorDialog());
        availBtn.addActionListener(e -> showAvailabilityDialog());
        delBtn.addActionListener(e -> deleteSelectedDoctor());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(availBtn);
        btnPanel.add(delBtn);
        add(btnPanel, BorderLayout.SOUTH);

        refreshData();
    }

    private void refreshData() {
        model.setRowCount(0);
        List<Doctor> docs = DoctorService.getInstance().getAllDoctors();
        for (Doctor d : docs) {
            model.addRow(
                    new Object[] { d.getId(), d.getName(), d.getEmail(), d.getSpecialty(), d.getConsultationFee() });
        }
    }

    private void deleteSelectedDoctor() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;
        int id = (int) model.getValueAt(row, 0);
        if (DoctorService.getInstance().deleteDoctor(id)) {
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete.");
        }
    }

    private void showAddDoctorDialog() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Doctor", true);
        d.setSize(300, 400);
        d.setLayout(new GridLayout(6, 2));

        JTextField nameF = new JTextField();
        JTextField emailF = new JTextField();
        JPasswordField passF = new JPasswordField();
        JTextField specF = new JTextField();
        JTextField feeF = new JTextField();

        d.add(new JLabel("Name:"));
        d.add(nameF);
        d.add(new JLabel("Email:"));
        d.add(emailF);
        d.add(new JLabel("Password:"));
        d.add(passF);
        d.add(new JLabel("Specialty:"));
        d.add(specF);
        d.add(new JLabel("Fee:"));
        d.add(feeF);

        JButton saveParams = new JButton("Save");
        saveParams.addActionListener(e -> {
            try {
                double fee = Double.parseDouble(feeF.getText());
                boolean res = DoctorService.getInstance().addDoctor(nameF.getText(), emailF.getText(),
                        new String(passF.getPassword()), specF.getText(), fee);
                if (res) {
                    d.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(d, "Error adding doctor.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Invalid input.");
            }
        });

        d.add(saveParams);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void showEditDoctorDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a doctor first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        Doctor doc = DoctorService.getInstance().findDoctorById(id).orElse(null);
        if (doc == null)
            return;

        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Doctor", true);
        d.setSize(300, 300);
        d.setLayout(new GridLayout(4, 2));

        JTextField specF = new JTextField(doc.getSpecialty());
        JTextField feeF = new JTextField(String.valueOf(doc.getConsultationFee()));

        d.add(new JLabel("Name (Read-only):"));
        d.add(new JLabel(doc.getName()));
        d.add(new JLabel("Specialty:"));
        d.add(specF);
        d.add(new JLabel("Fee:"));
        d.add(feeF);

        JButton saveParams = new JButton("Update");
        saveParams.addActionListener(e -> {
            try {
                double fee = Double.parseDouble(feeF.getText());
                boolean res = DoctorService.getInstance().updateDoctor(id, specF.getText(), fee);
                if (res) {
                    d.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(d, "Error updating doctor.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Invalid input.");
            }
        });

        d.add(saveParams);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void showAvailabilityDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a doctor first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        Doctor doc = DoctorService.getInstance().findDoctorById(id).orElse(null);
        if (doc == null)
            return;

        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manage Availability", true);
        d.setSize(450, 450);
        d.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(Theme.PRIMARY_COLOR);
        JLabel title = new JLabel("Availability for " + doc.getName());
        title.setFont(Theme.HEADER_FONT);
        title.setForeground(Theme.WHITE);
        header.add(title);
        d.add(header, BorderLayout.NORTH);

        // List
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String s : doc.getAvailability()) {
            listModel.addElement(s);
        }
        JList<String> list = new JList<>(listModel);
        list.setFont(Theme.REGULAR_FONT);
        d.add(new JScrollPane(list), BorderLayout.CENTER);

        // Controls Panel
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controls.setBackground(Theme.BACKGROUND_COLOR);

        // Input Row
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

        // Buttons Row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setBackground(Theme.BACKGROUND_COLOR);

        JButton addBtn = new JButton("Add Slot");
        Theme.styleButton(addBtn, true);

        JButton removeBtn = new JButton("Remove Selected");
        Theme.styleButton(removeBtn, false);
        removeBtn.setForeground(Theme.ERROR_COLOR);

        JButton saveBtn = new JButton("Save & Close");
        Theme.styleButton(saveBtn, false);

        addBtn.addActionListener(e -> {
            String day = (String) dayBox.getSelectedItem();
            String time = hourBox.getSelectedItem() + ":" + minBox.getSelectedItem();
            String slot = day + " " + time;

            if (!listModel.contains(slot)) {
                listModel.addElement(slot);
            } else {
                JOptionPane.showMessageDialog(d, "Slot already exists!");
            }
        });

        removeBtn.addActionListener(e -> {
            if (!list.isSelectionEmpty()) {
                listModel.remove(list.getSelectedIndex());
            }
        });

        saveBtn.addActionListener(e -> {
            List<String> newAvail = new ArrayList<>();
            for (int i = 0; i < listModel.size(); i++) {
                newAvail.add(listModel.get(i));
            }
            DoctorService.getInstance().updateDoctorAvailability(doc.getId(), newAvail);
            d.dispose();
        });

        btnRow.add(addBtn);
        btnRow.add(removeBtn);
        btnRow.add(saveBtn);

        controls.add(inputPanel);
        controls.add(Box.createVerticalStrut(10));
        controls.add(btnRow);

        d.add(controls, BorderLayout.SOUTH);

        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }
}
