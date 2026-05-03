package model;

public class Enrollment {

    public String studentUsername, courseCode;

    public Enrollment(String studentUsername, String courseCode){
        this.studentUsername = studentUsername;
        this.courseCode = courseCode;
    }

    //Getters

    public String getStudentUsername() {return studentUsername;}
    public String getCourseCode() {return courseCode;}

    //File-Saving
    public String toFileString(){
        return studentUsername + "," + courseCode;
    }

    @Override
    public String toString(){
        return toFileString();
    }

    //File-loading
    public static Enrollment fromFileString(String line){
        String[] parts = line.split(",");
        return new Enrollment(parts[0], parts[1]);
    }
}