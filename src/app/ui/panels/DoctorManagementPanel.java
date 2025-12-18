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
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND_COLOR);

        // Header
        JLabel title = new JLabel("Manage Doctors");
        title.setFont(Theme.HEADER_FONT);
        add(title, BorderLayout.NORTH);

        // Table
        String[] cols = { "ID", "Name", "Email", "Specialty", "Fee" };
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Controls
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add Doctor");
        JButton editBtn = new JButton("Edit Details");
        JButton availBtn = new JButton("Manage Availability");
        JButton delBtn = new JButton("Delete Doctor");

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
        d.setSize(400, 400);
        d.setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String s : doc.getAvailability()) {
            listModel.addElement(s);
        }
        JList<String> list = new JList<>(listModel);
        d.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel controls = new JPanel();
        JTextField slotInput = new JTextField(10);
        JButton addBtn = new JButton("Add Slot");
        JButton removeBtn = new JButton("Remove Selected");
        JButton saveBtn = new JButton("Save & Close");

        addBtn.addActionListener(e -> {
            String txt = slotInput.getText().trim();
            if (!txt.isEmpty()) {
                listModel.addElement(txt);
                slotInput.setText("");
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

        controls.add(new JLabel("Slot (Day HH:MM):"));
        controls.add(slotInput);
        controls.add(addBtn);
        controls.add(removeBtn);

        d.add(controls, BorderLayout.NORTH);
        d.add(saveBtn, BorderLayout.SOUTH);

        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }
}
