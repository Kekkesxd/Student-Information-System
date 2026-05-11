package ui;

import data.DataStore;
import main.UniversityAutoApp;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel{

    private DataStore ds;
    private UniversityAutoApp app;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPanel(DataStore ds, UniversityAutoApp app){
        this.ds = ds;
        this.app = app;

        setLayout(new GridBagLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        JPanel loginCard = new JPanel(new GridBagLayout());
        loginCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Login"),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Student Information System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginCard.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginCard.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(18);
        loginCard.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginCard.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        loginCard.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        loginCard.add(loginButton, gbc);

        loginButton.addActionListener(e -> handleLogin());
        app.getRootPane().setDefaultButton(loginButton);

        mainPanel.add(loginCard);
        add(mainPanel);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your username!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please Enter your password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.matches(".*\\s.*")) {
            JOptionPane.showMessageDialog(this, "Password cannot contain spaces!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User user = ds.authenticate(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        app.showPanelForUser(user);
    }
}