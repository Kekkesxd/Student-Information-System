package model;

public class StudentProfile {

    public String studentID, fullName, department, userName;
    public int year;

    public StudentProfile(String studentID, String fullName, String department, int year, String userName){
        this.studentID = studentID;
        this.fullName = fullName;
        this.department = department;
        this.year = year;
        this.userName = userName;
    }

    //Getters
    public String getStudentID() {return studentID;}
    public String getFullName() {return fullName;}
    public String getDepartment() {return department;}
    public int getYear() {return year;}
    public String getUserName() {return userName;}


    //File Saving
    public String toFileString(){
        return studentID + "," + fullName + "," + department + "," + year + "," + userName;
    }

    @Override
    public String toString(){
        return toFileString();
    }

    //File loading
    public static StudentProfile fromFileString(String line){
        String[] parts = line.split(",");
        return new StudentProfile(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4]);
    }
}