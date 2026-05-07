package ui;

import data.DataStore;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminPanel extends JFrame {

    private DataStore ds;
    private User currUser;

    private DefaultTableModel userTableModel, studentTableModel, courseTableModel, overviewTableModel;
    private JComboBox<String> studentUserCombo, instructorCombo;
    private JLabel totalUsersLabel, totalStudentsLabel, totalInstructorsLabel, totalCoursesLabel, totalEnrollmentsLabel;

    public AdminPanel(DataStore ds, User currUser) {
        this.ds = ds;
        this.currUser = currUser;

        setTitle("Admin Panel - " + currUser.fullName);
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel headPanel = new JPanel(new BorderLayout());

        JLabel headLabel = new JLabel("Admin Panel - Logged in as: " + currUser.fullName);
        headLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(logoutBtn);

        headPanel.add(headLabel, BorderLayout.CENTER);
        headPanel.add(rightPanel, BorderLayout.EAST);

        add(headPanel, BorderLayout.NORTH);

        studentUserCombo = new JComboBox<>();
        instructorCombo = new JComboBox<>();
        refreshCombos();

        JTabbedPane tabs = new JTabbedPane();



        tabs.addTab("Overview", createOverviewPanel());
        tabs.addTab("Users", createUsersPanel());
        tabs.addTab("Students", createStudentsPanel());
        tabs.addTab("Courses", createCoursePanel());


        tabs.addChangeListener(e -> {
            refreshUserTable();
            refreshStudentTable();
            refreshCourseTable();
            refreshOverview();
            refreshCombos();
        });

        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createUsersPanel() {
        //Users Tab
        JPanel usersPanel = new JPanel(new BorderLayout(10, 10));
        usersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Table
        String[] userColumns = {"Username", "Role", "Full Name", "Reference ID"};
        userTableModel = new DefaultTableModel(userColumns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable userTable = new JTable(userTableModel);
        styleTable(userTable);
        JScrollPane userScroll = new JScrollPane(userTable);

        JPanel addUserForm = new JPanel(new GridLayout(6, 2, 10, 10));

        JTextField newUsername = new JTextField();
        JTextField newPassword = new JTextField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"ADMIN", "INSTRUCTOR", "STUDENT"});
        JTextField newFullName = new JTextField();
        JTextField newRefID = new JTextField();
        JButton addUserBtn = new JButton("Add User");

        addUserForm.add(new JLabel("Username:"));
        addUserForm.add(newUsername);

        addUserForm.add(new JLabel("Password:"));
        addUserForm.add(newPassword);

        addUserForm.add(new JLabel("Role:"));
        addUserForm.add(roleCombo);

        addUserForm.add(new JLabel("Full Name:"));
        addUserForm.add(newFullName);

        addUserForm.add(new JLabel("Reference ID:"));
        addUserForm.add(newRefID);

        addUserForm.add(new JLabel());
        addUserForm.add(addUserBtn);

        addUserBtn.addActionListener(e -> {
            String uname = newUsername.getText().trim();
            String pass = newPassword.getText().trim();
            String role = (String) roleCombo.getSelectedItem();
            String fname = newFullName.getText().trim();
            String refid = newRefID.getText().trim();

            if (uname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter a Username", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter a Password", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (fname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter Full name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (ds.findUser(uname) != null) {
                JOptionPane.showMessageDialog(this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ds.users.add(new User(uname, pass, role, fname, refid));
            ds.saveUsers();

            newUsername.setText("");
            newPassword.setText("");
            newFullName.setText("");
            newRefID.setText("");

            refreshUserTable();
            refreshCombos();
            refreshOverview();

            JOptionPane.showMessageDialog(this, "User added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        refreshUserTable();

        addUserForm.setBorder(BorderFactory.createEmptyBorder(0, 0, 0,0));

        JPanel tableCard = createCardPanel("Users");
        tableCard.add(userScroll, BorderLayout.CENTER);

        JPanel formCard = createCardPanel("Add User");
        formCard.add(addUserForm, BorderLayout.CENTER);

        usersPanel.add(tableCard, BorderLayout.CENTER);
        usersPanel.add(formCard, BorderLayout.SOUTH);
        return usersPanel;
    }

    private JPanel createStudentsPanel() {

        //Students Tab
        JPanel studentsPanel = new JPanel(new BorderLayout(10, 10));
        studentsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Table
        String[] studentColumns = {"Student ID", "Full Name", "Department", "Year", "Username"};
        studentTableModel = new DefaultTableModel(studentColumns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable studentTable = new JTable(studentTableModel);
        styleTable(studentTable);
        JScrollPane studentScroll = new JScrollPane(studentTable);


        //Adding Student Form
        JPanel addStudentForm = new JPanel(new GridLayout(6, 2, 10, 10));

        JTextField newStudentID = new JTextField();
        JTextField newStudentName = new JTextField();
        JTextField newDepartment = new JTextField();
        JTextField newYear = new JTextField();
        JButton addStudentBtn = new JButton("Add Student");

        addStudentForm.add(new JLabel("Student ID:"));
        addStudentForm.add(newStudentID);

        addStudentForm.add(new JLabel("Full Name:"));
        addStudentForm.add(newStudentName);

        addStudentForm.add(new JLabel("Department:"));
        addStudentForm.add(newDepartment);

        addStudentForm.add(new JLabel("Year:"));
        addStudentForm.add(newYear);

        addStudentForm.add(new JLabel("Username:"));
        addStudentForm.add(studentUserCombo);

        addStudentForm.add(new JLabel());
        addStudentForm.add(addStudentBtn);

        addStudentBtn.addActionListener(e -> {
            String sid = newStudentID.getText().trim();
            String fullname = newStudentName.getText().trim();
            String dept = newDepartment.getText().trim();
            String yearStr = newYear.getText().trim();
            String username = (String) studentUserCombo.getSelectedItem();

            if (sid.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter your ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (fullname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter your name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dept.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter your Department", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (yearStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter The Year", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (username == null) {
                JOptionPane.showMessageDialog(this, "Please add a student user first", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (ds.findStudentByUser(username) != null) {
                JOptionPane.showMessageDialog(this, "This user already has a student profile", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (ds.findStudentById(sid) != null) {
                JOptionPane.showMessageDialog(this, "Student ID already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int year = Integer.parseInt(yearStr);

                if(year <= 0){
                    JOptionPane.showMessageDialog(this, "Year must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                ds.students.add(new StudentProfile(sid, fullname, dept, year, username));
                ds.saveStudents();

                newStudentID.setText("");
                newStudentName.setText("");
                newDepartment.setText("");
                newYear.setText("");

                refreshStudentTable();
                refreshOverview();

                JOptionPane.showMessageDialog(this, "Student Profile added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Year must be a number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addStudentForm.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        JPanel tableCard = createCardPanel("Student Profiles");
        tableCard.add(studentScroll, BorderLayout.CENTER);

        JPanel formCard = createCardPanel("Add Student Profile");
        formCard.add(addStudentForm, BorderLayout.CENTER);

        studentsPanel.add(tableCard, BorderLayout.CENTER);
        studentsPanel.add(formCard, BorderLayout.SOUTH);

        refreshStudentTable();

        return studentsPanel;
    }

    private JPanel createCoursePanel() {

        //Courses Tab
        JPanel coursesPanel = new JPanel(new BorderLayout(10, 10));
        coursesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Table
        String[] coursesColumns = {"Course Code", "Course Name", "Credit", "Quota", "Instructor"};
        courseTableModel = new DefaultTableModel(coursesColumns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable courseTable = new JTable(courseTableModel);
        styleTable(courseTable);
        JScrollPane courseScroll = new JScrollPane(courseTable);

        JPanel addCourseForm = new JPanel(new GridLayout(6, 2, 10, 10));

        JTextField newCourseCode = new JTextField();
        JTextField newCourseName = new JTextField();
        JTextField newCredit = new JTextField();
        JTextField newQuota = new JTextField();
        JButton addCourseBtn = new JButton("Add Course");

        addCourseForm.add(new JLabel("Course Code:"));
        addCourseForm.add(newCourseCode);

        addCourseForm.add(new JLabel("Course Name:"));
        addCourseForm.add(newCourseName);

        addCourseForm.add(new JLabel("Credit:"));
        addCourseForm.add(newCredit);

        addCourseForm.add(new JLabel("Quota:"));
        addCourseForm.add(newQuota);

        addCourseForm.add(new JLabel("Instructor:"));
        addCourseForm.add(instructorCombo);

        addCourseForm.add(new JLabel());
        addCourseForm.add(addCourseBtn);

        addCourseBtn.addActionListener(e -> {
            String code = newCourseCode.getText().trim();
            String name = newCourseName.getText().trim();
            String creditStr = newCredit.getText().trim();
            String quotaStr = newQuota.getText().trim();
            String instructor = (String) instructorCombo.getSelectedItem();

            if (instructor == null) {
                JOptionPane.showMessageDialog(this, "Please add an instructor user first", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter Course Code", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (ds.findCourse(code) != null) {
                JOptionPane.showMessageDialog(this, "Course Code already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter Course Name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (creditStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter the amount of credits", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (quotaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter The Quota", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int credit = Integer.parseInt(creditStr);
                int quota = Integer.parseInt(quotaStr);

                if (credit <= 0) {
                    JOptionPane.showMessageDialog(this, "Credit must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (quota <= 0) {
                    JOptionPane.showMessageDialog(this, "Quota must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ds.courses.add(new Course(code, name, credit, quota, instructor));
                ds.saveCourses();

                newCourseCode.setText("");
                newCourseName.setText("");
                newCredit.setText("");
                newQuota.setText("");

                refreshCourseTable();
                refreshOverview();

                JOptionPane.showMessageDialog(this, "Course added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Credit and Quota must be numbers", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addCourseForm.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        JPanel tableCard = createCardPanel("Courses");
        tableCard.add(courseScroll, BorderLayout.CENTER);

        JPanel formCard = createCardPanel("Add Course");
        formCard.add(addCourseForm, BorderLayout.CENTER);

        coursesPanel.add(tableCard, BorderLayout.CENTER);
        coursesPanel.add(formCard, BorderLayout.SOUTH);

        refreshCourseTable();

        return coursesPanel;
    }

    private JPanel createOverviewPanel() {
        JPanel overviewPanel = new JPanel(new BorderLayout(10, 10));
        overviewPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        totalUsersLabel = new JLabel();
        totalStudentsLabel = new JLabel();
        totalInstructorsLabel = new JLabel();
        totalCoursesLabel = new JLabel();
        totalEnrollmentsLabel = new JLabel();

        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 15, 15));
        statsPanel.add(createStatCard("Total Users:", totalUsersLabel));
        statsPanel.add(createStatCard("Students:", totalStudentsLabel));
        statsPanel.add(createStatCard("Instructors:", totalInstructorsLabel));
        statsPanel.add(createStatCard("Courses:", totalCoursesLabel));
        statsPanel.add(createStatCard("Enrollments:", totalEnrollmentsLabel));

        JPanel statsCard = createCardPanel("System Overview");
        statsCard.add(statsPanel, BorderLayout.CENTER);

        String[] overviewColumns = {"Course Code", "Course Name", "Instructor", "Quota", "Enrolled"};
        overviewTableModel = new DefaultTableModel(overviewColumns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable overviewTable = new JTable(overviewTableModel);
        styleTable(overviewTable);
        JScrollPane overviewScroll = new JScrollPane(overviewTable);

        JPanel tableCard = createCardPanel("Course Enrollment Summary");
        tableCard.add(overviewScroll, BorderLayout.CENTER);

        overviewPanel.add(statsCard, BorderLayout.NORTH);
        overviewPanel.add(tableCard, BorderLayout.CENTER);

        refreshOverview();

        return overviewPanel;
    }

    private void refreshUserTable() {
        if (userTableModel == null) return;

        userTableModel.setRowCount(0);

        for (User u : ds.users) {
            userTableModel.addRow(new Object[]{u.username, u.role, u.fullName, u.refID});
        }
    }

    private void refreshStudentTable() {
        if (studentTableModel == null) return;

        studentTableModel.setRowCount(0);

        for (StudentProfile s : ds.students) {
            studentTableModel.addRow(new Object[]{s.studentId, s.fullName, s.department, s.year, s.username});
        }
    }

    private void refreshCourseTable() {
        if (courseTableModel == null) return;

        courseTableModel.setRowCount(0);

        for (Course c : ds.courses) {
            courseTableModel.addRow(new Object[]{c.courseCode, c.courseName, c.credit, c.quota, c.instructorUsername});
        }
    }

    private void refreshOverview() {
        if (totalUsersLabel == null || overviewTableModel == null) return;

        int studentCount = 0;
        int instructorCount = 0;

        for (User u : ds.users) {
            if (u.role.equals("STUDENT")) {
                studentCount++;
            } else if (u.role.equals("INSTRUCTOR")) {
                instructorCount++;
            }
        }

        totalUsersLabel.setText(String.valueOf(ds.users.size()));
        totalStudentsLabel.setText(String.valueOf(studentCount));
        totalInstructorsLabel.setText(String.valueOf(instructorCount));
        totalCoursesLabel.setText(String.valueOf(ds.courses.size()));
        totalEnrollmentsLabel.setText(String.valueOf(ds.enrollments.size()));

        overviewTableModel.setRowCount(0);

        for (Course c : ds.courses) {
            int enrolledCount = ds.countEnrollmentForCourse(c.courseCode);

            overviewTableModel.addRow(new Object[]{
                    c.courseCode,
                    c.courseName,
                    c.instructorUsername,
                    c.quota,
                    enrolledCount
            });
        }
    }

    private void refreshCombos() {
        if (studentUserCombo == null || instructorCombo == null) return;

        studentUserCombo.removeAllItems();
        instructorCombo.removeAllItems();

        for (User u : ds.users) {
            if (u.role.equals("STUDENT")) {
                studentUserCombo.addItem(u.username);
            } else if (u.role.equals("INSTRUCTOR")) {
                instructorCombo.addItem(u.username);
            }
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
    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }
}