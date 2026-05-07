package ui;

import data.DataStore;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InstructorPanel extends JFrame{
    private DataStore ds;
    private User currUser;

    private DefaultTableModel myCoursesModel, gradesModel;

    private JComboBox<String> courseCombo;
    private JTextField midField, finalField;

    public InstructorPanel(DataStore ds, User currUser){
        this.ds = ds;
        this.currUser = currUser;

        setTitle("Instructor Panel - " +currUser.fullName);
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel headPanel = new JPanel(new BorderLayout());

        JLabel headLabel = new JLabel("Instructor Panel - Logged in as: " + currUser.fullName);
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

        tabs.addTab("My Courses" , createCoursesPanel());
        tabs.addTab("Grade Entry", createGradePanel());

        tabs.addChangeListener(e->{
            refreshMyCoursesTable();
            refreshCourseCombo();
            refreshGradeTable();
        });

        add(tabs, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createCoursesPanel(){
        JPanel panel = new JPanel(new BorderLayout(10 ,10));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        String[] columns = {"Course Code", "Course Name", "Credit", "Quota", "Enrolled"};
        myCoursesModel = new DefaultTableModel(columns, 0){
            public boolean isCellEditable(int row, int col){
                return false;
            }
        };

        JTable table = new JTable(myCoursesModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel tableCard= createCardPanel("Assigned Courses");
        tableCard.add(scrollPane, BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.CENTER);

        refreshMyCoursesTable();

        return panel;
    }

    private JPanel createGradePanel(){
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10,10,10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        courseCombo = new JComboBox<>();
        JButton loadStudentBtn = new JButton("Load Students");

        topPanel.add(new JLabel("Course: "));
        topPanel.add(courseCombo);
        topPanel.add(loadStudentBtn);

        String[] columns = {"Student Username", "Student Name", "Course Code", "Midterm", "Final", "Average", "Letter"};
        gradesModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable studentTable = new JTable(gradesModel);
        styleTable(studentTable);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        studentTable.getSelectionModel().addListSelectionListener(e-> {
            if(!e.getValueIsAdjusting()){
                int selectedRow = studentTable.getSelectedRow();

                if(selectedRow != -1){
                    Object midValue = gradesModel.getValueAt(selectedRow, 3);
                    Object finalexamValue = gradesModel.getValueAt(selectedRow, 4);

                    midField.setText(midValue == null ? "" : midValue.toString());
                    finalField.setText(finalexamValue == null ? "" : finalexamValue.toString());
                }
            }
        });

        JPanel bottomPanel = new JPanel(new GridLayout(2,4,10,10));

        midField = new JTextField();
        finalField = new JTextField();
        JButton saveGradeBtn = new JButton("Save Grade");

        saveGradeBtn.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();

            if(selectedRow == -1){
                JOptionPane.showMessageDialog(this, "Please select a student First.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sUsername = gradesModel.getValueAt(selectedRow, 0).toString();
            String courseCode = gradesModel.getValueAt(selectedRow, 2).toString();

            saveGrade(sUsername, courseCode);
        });

        bottomPanel.add(new JLabel("Midterm:"));
        bottomPanel.add(midField);

        bottomPanel.add(new JLabel("Final:"));
        bottomPanel.add(finalField);

        bottomPanel.add(new JLabel());
        bottomPanel.add(new JLabel());
        bottomPanel.add(new JLabel());
        bottomPanel.add(saveGradeBtn);

        loadStudentBtn.addActionListener(e-> refreshGradeTable());
        courseCombo.addActionListener(e-> refreshGradeTable());

        JPanel courseCard = createCardPanel("Select Course");
        courseCard.add(topPanel, BorderLayout.CENTER);

        JPanel studentCard = createCardPanel("Students in Selected Course");
        studentCard.add(scrollPane, BorderLayout.CENTER);

        JPanel gradeCard = createCardPanel("Enter / Update Grade");
        gradeCard.add(bottomPanel, BorderLayout.CENTER);

        panel.add(courseCard, BorderLayout.NORTH);
        panel.add(studentCard, BorderLayout.CENTER);
        panel.add(gradeCard, BorderLayout.SOUTH);

        refreshCourseCombo();
        refreshGradeTable();

        return panel;
    }
    private void refreshMyCoursesTable() {
        if (myCoursesModel == null) return;

        myCoursesModel.setRowCount(0);

        for (Course c : ds.getCoursesByInstructor(currUser.username)) {
            int enrolledCount = ds.countEnrollmentForCourse(c.courseCode);

            myCoursesModel.addRow(new Object[]{
                    c.courseCode,
                    c.courseName,
                    c.credit,
                    c.quota,
                    enrolledCount
            });
        }
    }

    private void refreshCourseCombo() {
        if (courseCombo == null) return;

        courseCombo.removeAllItems();

        for (Course c : ds.getCoursesByInstructor(currUser.username)) {
            courseCombo.addItem(c.courseCode);
        }
    }

    private void refreshGradeTable(){
        if(gradesModel == null || courseCombo == null) return;

        gradesModel.setRowCount(0);

        String selectedCourseCode = (String) courseCombo.getSelectedItem();

        if(selectedCourseCode == null)return;

        for(Enrollment enrollment : ds.getEnrollmentByCourse(selectedCourseCode)){
            User sUser = ds.findUser(enrollment.studentUsername);
            GradeRecord grade = ds.findGrade(enrollment.studentUsername, selectedCourseCode);

            String sName = sUser != null ? sUser.fullName : "Unknown";

            Object midValue = "";
            Object finalexamValue = "";
            Object avgValue = "";
            Object letterValue = "";

            if(grade != null){
                midValue = grade.midterm;
                finalexamValue = grade.finalexam;
                avgValue = String.format("%.2f", grade.calcAverage());
                letterValue = grade.getLetterGrade();
            }

            gradesModel.addRow(new Object[]{enrollment.studentUsername, sName, selectedCourseCode,midValue, finalexamValue, avgValue,letterValue});
        }
    }

    private void saveGrade(String studentUsername, String courseCode) {
        String midtermText = midField.getText().trim();
        String finalText = finalField.getText().trim();

        if (midtermText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter midterm grade.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (finalText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter final grade.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double midterm = Double.parseDouble(midtermText);
            double finalExam = Double.parseDouble(finalText);

            if (midterm < 0 || midterm > 100) {
                JOptionPane.showMessageDialog(this, "Grade can't be negative or above 100.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (finalExam < 0 || finalExam > 100) {
                JOptionPane.showMessageDialog(this, "Grade can't be negative or above 100.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ds.upsertGrade(studentUsername, courseCode, midterm, finalExam);

            refreshGradeTable();

            midField.setText("");
            finalField.setText("");

            JOptionPane.showMessageDialog(this, "Grade saved successfully.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Grades must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    private JPanel createButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        for (JButton button : buttons) {
            panel.add(button);
        }

        return panel;
    }
}