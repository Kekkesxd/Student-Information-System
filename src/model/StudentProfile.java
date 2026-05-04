package model;

public class StudentProfile {

    public String studentId, fullName, department, username;
    public int year;

    public StudentProfile(String studentId, String fullName, String department, int year, String username){
        this.studentId = studentId;
        this.fullName = fullName;
        this.department = department;
        this.year = year;
        this.username = username;
    }

    //Getters
    public String getStudentID() {return studentId;}
    public String getFullName() {return fullName;}
    public String getDepartment() {return department;}
    public int getYear() {return year;}
    public String getUsername() {return username;}


    //File Saving
    public String toFileString(){
        return studentId + "," + fullName + "," + department + "," + year + "," + username;
    }

    @Override
    public String toString(){
        return toFileString();
    }

    //File loading
    public static StudentProfile fromFileString(String line){
        String[] parts = line.split(",", -1);
        return new StudentProfile(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4]);
    }
}