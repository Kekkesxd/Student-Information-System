package main;

import data.DataStore;
import ui.LoginFrame;

import javax.swing.*;

public class UniversityAutoApp {
    public static DataStore ds = new DataStore();

    public static void main(String[] args) {
        ds.initialize();

        SwingUtilities.invokeLater(() -> new LoginFrame(ds));

    }
}