package main;

import data.DataStore;
import ui.*;
import model.*;

import javax.swing.*;

public class UniversityAutoApp extends JFrame {
    public static DataStore ds = new DataStore();

    public UniversityAutoApp() {
        setTitle("University Automation App");
        setSize(950, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        showLoginPanel();

        setVisible(true);
    }

    public void showLoginPanel() {
        setContentPane(new LoginPanel(ds, this));
        refreshFrame();
    }

    public void showPanelForUser(User user) {
        switch (user.role) {
            case "ADMIN":
                setContentPane(new AdminPanel(ds, user, this));
                break;

            case "STUDENT":
                setContentPane(new StudentPanel(ds, user, this));
                break;

            case "INSTRUCTOR":
                setContentPane(new InstructorPanel(ds, user, this));
                break;

            default:
                JOptionPane.showMessageDialog(
                        this,
                        "Unknown role: " + user.role,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                showLoginPanel();
                break;
        }

        refreshFrame();
    }

    public void logout() {
        showLoginPanel();
    }

    private void refreshFrame() {
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        ds.initialize();

        SwingUtilities.invokeLater(() -> new UniversityAutoApp());

    }
}