package ui;

import data.DataStore;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame{

    private DataStore ds;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame(DataStore ds){
        this.ds = ds;
        setTitle("University Automation App");
        setSize(350,200 );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10 ,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20 ,20));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        panel.add(new JLabel());
        panel.add(loginButton);

        loginButton.addActionListener(e-> handleLogin());

        add(panel);
    }

    private void handleLogin(){
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if(username.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter your username!", "Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(password.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please Enter your password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = ds.authenticate(username, password);
        if(user == null){
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.dispose();
        switch (user.role){
            case "ADMIN":
                new AdminPanel(ds, user);
                break;
        }
        JOptionPane.showMessageDialog(this, "Welcome " + user.fullName, "Success", JOptionPane.INFORMATION_MESSAGE);
    }



}