


package main;

import data.DataStore;
import ui.LoginFrame;

import javax.swing.*;

public class UniversityAutoApp {
    public static DataStore ds = new DataStore();

    public static void main(String[] args) {
        ds.initialize();

        if(ds.users.isEmpty()){
            ds.users.add(new model.User("admin", "admin123", "ADMIN", "System Admin", ""));
            ds.saveUsers();
        }

        SwingUtilities.invokeLater(() -> new LoginFrame(ds));

    }
}