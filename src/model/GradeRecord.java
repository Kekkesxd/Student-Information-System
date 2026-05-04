package model;

public class GradeRecord {

    public String studentUsername, courseCode;
    public double midterm, finalexam;

    public GradeRecord(String studentUsername, String courseCode, double midterm, double finalexam){
        this.studentUsername = studentUsername;
        this.courseCode = courseCode;
        this.midterm = midterm;
        this.finalexam = finalexam;
    }

    //Getters
    public String getStudentUsername() {return studentUsername;}
    public String getCourseCode() {return courseCode;}
    public double getMidterm() {return midterm;}
    public double getFinalexam() {return finalexam;}


    public double calcAverage(){
        double avg = midterm * 0.4 + finalexam * 0.6;
        return avg;
    }

    public String getLetterGrade(){
        double avg = calcAverage();
        String grade;
        if(avg >= 85 && avg <= 100) {
            grade = "AA";
        } else if (avg >= 80 && avg < 85 ) {
            grade = "BA";
        } else if (avg >= 70 && avg <80) {
            grade = "BB";
        } else if (avg >= 65 && avg < 70) {
            grade = "CB";
        } else if (avg >= 60 && avg < 65) {
            grade = "CC";
        } else if (avg >= 50 && avg < 60) {
            grade = "DC";
        } else if (avg >=40 && avg < 50 ) {
            grade = "DD";
        } else if (avg >= 30 && avg < 40) {
            grade = "FD";
        } else if (avg >= 0 && avg < 30) {
            grade = "FF";
        } else {
            grade = "Invalid grade";
        }
        return grade;
    }
    //File-Saving
    public String toFileString(){
        return studentUsername + "," + courseCode + "," + midterm + "," + finalexam;
    }

    @Override
    public String toString(){
        return toFileString();
    }

    //File-loading
    public static GradeRecord fromFileString(String line){
        String[] parts = line.split(",");
        return new GradeRecord(parts[0], parts[1],Double.parseDouble(parts[2]),Double.parseDouble(parts[3]));
    }
}