


package main;

import data.DataStore;
import model.*;

public class UniversityAutoApp {
    public static void main(String[] args) {

        //Temporary Test for Data store
        DataStore ds = new DataStore();
        ds.initialize(); // loads files (will be empty first run, that's fine)

        // Add a test user manually
        ds.users.add(new User("john", "1234", "STUDENT", "John Doe", "S001"));
        ds.saveUsers(); // save to file

        // Try authenticating
        User user = ds.authenticate("john", "1234");
        if (user != null) {
            System.out.println("Login successful! Role: " + user.role);
        } else {
            System.out.println("Login failed!");
        }
    }
}