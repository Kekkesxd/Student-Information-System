package ui;

import data.DataStore;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentPanel extends JFrame{
    private DataStore ds;
    private User currUser;

    private DefaultTableModel availCoursesModel, myCoursesModel, transcModel;

    private JLabel gpaLabel;

    public StudentPanel(DataStore ds, User currUser){
        this.ds = ds;
        this.currUser = currUser;

        setTitle("Student Panel " + currUser.fullName);
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel headPanel = new JPanel(new BorderLayout());

        JLabel headLabel = new JLabel("Student Panel - Logged in as: " + currUser.fullName);
        headLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(logoutBtn);

        headPanel.add(headLabel, BorderLayout.CENTER);
        headPanel.add(rightPanel, BorderLayout.EAST);

        add(headPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("My Courses", createMyCoursesPanel());
        tabs.addTab("Available Courses", createAvailCoursesPanel());
        tabs.addTab("Transcript", createTranscriptPanel());

        tabs.addChangeListener(e -> {
            refreshAvailCoursesTable();
            refreshMyCoursesTable();
            refreshTranscriptTable();
        });

        add(tabs, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createAvailCoursesPanel(){
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10 , 10, 10));

        String[] columns = {"Course Code", "Course Name", "Credit", "Quota", "Instructor", "Enrolled"};
        availCoursesModel = new DefaultTableModel(columns, 0){
            public boolean isCellEditable(int row, int col){
                return false;
            }
        };

        JTable table = new JTable(availCoursesModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton enrollButton = new JButton("Enroll Selected Course");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(enrollButton);

        enrollButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();

            if(selectedRow == -1){
                JOptionPane.showMessageDialog(this, "Please select a course first." , "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String courseCode = availCoursesModel.getValueAt(selectedRow, 0).toString();

            enrollInCourse(courseCode);
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshAvailCoursesTable();

        return panel;

    }
    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Course Code", "Course Name", "Credit", "Instructor"};
        myCoursesModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable table = new JTable(myCoursesModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton dropCourseBtn = new JButton("Drop Selected Course");

        dropCourseBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a course first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String courseCode = myCoursesModel.getValueAt(selectedRow, 0).toString();

            dropCourse(courseCode);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(dropCourseBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshMyCoursesTable();

        return panel;
    }

    private JPanel createTranscriptPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Course Code", "Course Name", "Credit", "Midterm", "Final", "Average", "Letter Grade"};
        transcModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = new JTable(transcModel);
        JScrollPane scrollPane = new JScrollPane(table);

        gpaLabel = new JLabel("GPA: 0.00");
        gpaLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(gpaLabel);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        refreshTranscriptTable();

        return panel;
    }


    private void refreshAvailCoursesTable(){
        if(availCoursesModel == null) return;

        availCoursesModel.setRowCount(0);

        for(Course c : ds.courses){
            int enrolledCount = ds.countEnrollmentForCourse(c.courseCode);

            availCoursesModel.addRow(new Object[]{c.courseCode, c.courseName, c.credit, c.quota, c.instructorUsername,enrolledCount});
        }
    }

    private void refreshMyCoursesTable(){
        if (myCoursesModel == null) return;

        myCoursesModel.setRowCount(0);

        for(Enrollment enrollment : ds.getEnrollmentByStudent(currUser.username)){
            Course c = ds.findCourse(enrollment.courseCode);

            if(c != null){
                myCoursesModel.addRow(new Object[]{c.courseCode, c.courseName, c.credit, c.instructorUsername });
            }
        }
    }

    private void refreshTranscriptTable(){
        if(transcModel == null) return;

        transcModel.setRowCount(0);

        for(GradeRecord g : ds.getGrades(currUser.username)){
            Course c = ds.findCourse(g.courseCode);

            String courseName = c != null ? c.courseName : "Unknown";
            int credits = c!= null ? c.credit : 0;

            transcModel.addRow(new Object[]{g.courseCode, courseName, credits, g.midterm, g.finalexam, String.format("%.2f", g.calcAverage()), g.getLetterGrade()});
        }

        if(gpaLabel != null){
            gpaLabel.setText(String.format("GPA: %.2f", ds.calculateGPA(currUser.username)));
        }
    }

    private void enrollInCourse(String courseCode){
        Course c = ds.findCourse(courseCode);

        if(c == null){
            JOptionPane.showMessageDialog(this, "Course not Found." ,"Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (ds.isStudentEnrolled(currUser.username, courseCode)){
            JOptionPane.showMessageDialog(this, "You are already enrolled in this course", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int enrolledCount = ds.countEnrollmentForCourse(courseCode);

        if(enrolledCount >= c.quota){
            JOptionPane.showMessageDialog(this, "This Course quota is full", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ds.enrollments.add(new Enrollment(currUser.username, courseCode));
        ds.saveEnrollments();

        refreshAvailCoursesTable();
        refreshMyCoursesTable();

        JOptionPane.showMessageDialog(this, "Enrolled Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    private void dropCourse(String courseCode) {
        GradeRecord grade = ds.findGrade(currUser.username, courseCode);

        if (grade != null) {
            JOptionPane.showMessageDialog(this,
                    "You cannot drop this course because a grade has already been entered.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to drop " + courseCode + "?",
                "Confirm Drop",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        ds.removeEnrollment(currUser.username, courseCode);

        refreshAvailCoursesTable();
        refreshMyCoursesTable();
        refreshTranscriptTable();

        JOptionPane.showMessageDialog(this, "Course dropped successfully.");
    }
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginFrame(ds);
        }
    }
}