package data;

import model.*;
import java.io.*;
import java.util.*;

public class DataStore {

    public List<User> users = new ArrayList<>();
    public List<StudentProfile> students = new ArrayList<>();
    public List<Course> courses = new ArrayList<>();
    public List<Enrollment> enrollments = new ArrayList<>();
    public List<GradeRecord> grades = new ArrayList<>();

    //File Pathing
    private static final String USERS_FILE = "data/users.txt";
    private static final String STUDENTS_FILE = "data/students.txt";
    private static final String COURSES_FILE = "data/courses.txt";
    private static final String ENROLLMENTS_FILE = "data/enrollments.txt";
    private static final String GRADES_FILE = "data/grades.txt";


    public void initialize() {
        loadUsers();
        loadStudents();
        loadCourses();
        loadEnrollments();
        loadGrades();
    }

    //Save Method
    public void saveUsers() {
        saveToFile(USERS_FILE, users);
    }

    public void saveStudents() {
        saveToFile(STUDENTS_FILE, students);
    }

    public void saveCourses() {
        saveToFile(COURSES_FILE, courses);
    }

    public void saveEnrollments() {
        saveToFile(ENROLLMENTS_FILE, enrollments);
    }

    public void saveGrades() {
        saveToFile(GRADES_FILE, grades);
    }

    //Helper
    //-- Aceepts any of type of list <T>
    //Easier than having 5 different saving methods
    private <T> void saveToFile(String path, List<T> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            for (T item : list) {
                pw.println(item.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving to " + path + ": " + e.getMessage());
        }
    }

    //Loading Method
    public void loadUsers() {
        users.clear();
        for (String line : readLines(USERS_FILE))
            users.add(User.fromFileString(line));
    }

    public void loadStudents() {
        students.clear();
        for (String line : readLines(STUDENTS_FILE))
            students.add(StudentProfile.fromFileString(line));
    }

    public void loadCourses() {
        courses.clear();
        for (String line : readLines(COURSES_FILE))
            courses.add(Course.fromFileString(line));
    }

    public void loadEnrollments() {
        enrollments.clear();
        for (String line : readLines(ENROLLMENTS_FILE))
            enrollments.add(Enrollment.fromFileString(line));
    }

    public void loadGrades() {
        grades.clear();
        for (String line : readLines(GRADES_FILE))
            grades.add(GradeRecord.fromFileString(line));
    }


    //Helper - reading lines from a file, skips empty lines
    private List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) return lines; // first run, files doesn't exist yet
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty())
                    lines.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading " + path + ": " + e.getMessage());
        }
        return lines;
    }

    //Login screen authentication
    public User authenticate(String username, String password) {
        for (User user : users) {
            if (user.username.equals(username) && user.password.equals(password)) {
                return user;
            }
        }
        return null;
    }

    public User findUser(String username) {
        for (User user : users) {
            if (user.username.equals(username)) {
                return user;
            }
        }
        return null;
    }

    //For searching the student list by username
    public StudentProfile findStudentByUser(String username) {
        for (StudentProfile student : students) {
            if (student.username.equals(username)) {
                return student;
            }
        }
        return null;
    }

    public StudentProfile findStudentById(String studentId){
        for(StudentProfile student : students){
            if(student.studentId.equals(studentId)){
                return student;
            }
        }
        return null;
    }

    public Course findCourse(String courseCode) {
        for (Course course : courses) {
            if (course.courseCode.equals(courseCode)) {
                return course;
            }
        }
        return null;
    }

    //List because instructor can be teaching multiple courses
    public List<Course> getCoursesByInstructor(String instructorUsername) {
        List<Course> result = new ArrayList<>();
        for (Course course : courses) {
            if (course.instructorUsername.equals(instructorUsername)) {
                result.add(course);
            }
        }
        return result;
    }

    //counts how many students are enrolled
    public int countEnrollmentForCourse(String courseCode) {
        int count = 0;
        for (Enrollment enrollment : enrollments) {
            if (enrollment.courseCode.equals(courseCode)) {
                count++;
            }
        }
        return count;
    }

    //Checks for enrollments and if student is in specific course or not
    public boolean isStudentEnrolled(String studentUsername, String courseCode) {
        for (Enrollment enrollment : enrollments) {
            if (enrollment.studentUsername.equals(studentUsername) && enrollment.courseCode.equals(courseCode)) {
                return true;
            }
        }
        return false;
    }

    public List<Enrollment> getEnrollmentByStudent(String studentUsername) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.studentUsername.equals(studentUsername)) {
                result.add(enrollment);
            }
        }
        return result;
    }

    public List<Enrollment> getEnrollmentByCourse(String courseCode) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.courseCode.equals(courseCode)) {
                result.add(enrollment);
            }
        }
        return result;
    }

    public void removeEnrollment(String studentUsername, String courseCode) {
        enrollments.removeIf(e -> e.studentUsername.equals(studentUsername) && e.courseCode.equals(courseCode)); //built-in method that removes if true
        saveEnrollments();
    }

    public GradeRecord findGrade(String studentUsername, String courseCode) {
        for (GradeRecord grade : grades) {
            if (grade.studentUsername.equals(studentUsername) && grade.courseCode.equals(courseCode)) {
                return grade;
            }
        }
        return null;
    }

    public List<GradeRecord> getGrades(String studentUsername){
        List<GradeRecord> result = new ArrayList<>();
        for(GradeRecord grade : grades){
            if(grade.studentUsername.equals(studentUsername))
            {
                result.add(grade);
            };
        }
        return result;
    }

    public void upsertGrade(String studentUsername, String courseCode, double midterm, double finalExam){
        GradeRecord existing = findGrade(studentUsername, courseCode);
        if(existing != null){
            existing.midterm =midterm;
            existing.finalexam =finalExam;
        }else {
            grades.add(new GradeRecord(studentUsername, courseCode, midterm, finalExam));
        }
        saveGrades();
    }

    public double calculateGPA(String studentUsername){
        List<GradeRecord> studentGrades = getGrades(studentUsername);
        if(studentGrades.isEmpty()) return 0.0;

        double totalPoints = 0;
        int totalCredits = 0;

        for(GradeRecord grade : studentGrades){
            Course course = findCourse(grade.courseCode);
            if(course != null){
                totalPoints += grade.calcAverage() * course.credit;
                totalCredits += course.credit;
            }
        }
        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }
}