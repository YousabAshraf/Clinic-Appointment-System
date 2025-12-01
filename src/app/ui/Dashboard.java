package app.ui;

import javax.swing.*;
import java.awt.*;
import app.security.SessionManager;
import app.models.User;

public class Dashboard extends JFrame {

    public Dashboard() {
        User current = SessionManager.getInstance().getLoggedUser();
        if(current == null) {
            JOptionPane.showMessageDialog(this, "No user logged in!");
            new LoginForm().setVisible(true); // ترجع على LoginForm
            this.dispose();
            return;
        }

        setTitle(current.getRole() + " Dashboard");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + current.getName() + "!" + current.getRole() , JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcomeLabel, BorderLayout.CENTER);

        // Logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(100,30));
        logoutBtn.addActionListener(e -> {
            SessionManager.getInstance().logout();
            new LoginForm().setVisible(true); // ارجع على LoginForm
            this.dispose();
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(logoutBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        add(panel);
    }
}
