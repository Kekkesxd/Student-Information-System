package model;

public class User {

    public String username, password, role, fullName, refID;

    public User(String username, String password, String role, String fullName, String refID){
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.refID = refID;
    }
    //Getters

    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getRole() {return role;}
    public String getFullName() {return fullName;}
    public String getRefID() {return refID;}


    //File-Saving
    public String toFileString(){
        return username + "," + password + "," + role + "," + fullName + "," + refID;
    }

    @Override
    public String toString(){
        return toFileString();
    }

    //File-loading
    public static User fromFileString(String line){
        String[] parts = line.split(",");
        return new User(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }
}