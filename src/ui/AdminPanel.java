package ui;

import data.DataStore;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminPanel extends JFrame{

    private DataStore ds;
    private User currUser;

    public AdminPanel(DataStore ds, User currUser){
        this.ds = ds;
        this.currUser = currUser;

        setTitle("Admin Panel -" + currUser.fullName);
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        JTabbedPane tabs = new JTabbedPane();

        //Users Tab
        JPanel usersPanel = new JPanel(new BorderLayout(10, 10));

        //Table
        String[] userColumns = {"Username", "Role","Full Name"};
        DefaultTableModel userTableModel = new DefaultTableModel(userColumns, 0){
            public boolean isCellEditable(int row, int col) {return false;}
        };

        JTable userTable = new JTable(userTableModel);
        JScrollPane userScroll = new JScrollPane(userTable);

        for (User u : ds.users){
            userTableModel.addRow(new Object[]{u.username, u.role, u.fullName});
        }

        JPanel addUserForm = new JPanel(new GridLayout(6, 2, 10, 10));
        addUserForm.setBorder(BorderFactory.createTitledBorder("Add User"));

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
        addUserForm.add(new JLabel("Ref ID:"));
        addUserForm.add(newRefID);
        addUserForm.add(new JLabel());
        addUserForm.add(addUserBtn);

        addUserBtn.addActionListener( e-> {
            String uname = newUsername.getText().trim();
            String pass = newPassword.getText().trim();
            String role = (String) roleCombo.getSelectedItem();
            String fname = newFullName.getText().trim();
            String refid = newRefID.getText().trim();

            if(uname.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please Enter a Username!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(pass.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please Enter a Password!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(fname.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please Enter Full name!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(ds.findUser(uname) != null){
                JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ds.users.add(new User(uname, pass, role, fname, refid));
            ds.saveUsers();
            userTableModel.addRow(new Object[]{uname, role, fname});
            newUsername.setText("");
            newPassword.setText("");
            newFullName.setText("");
            newRefID.setText("");
            JOptionPane.showMessageDialog(this, "User added successfully!", "Success",JOptionPane.INFORMATION_MESSAGE);
        });
        usersPanel.add(userScroll, BorderLayout.CENTER);
        usersPanel.add(addUserForm, BorderLayout.SOUTH);
        tabs.addTab("Users", usersPanel);

        //Students Tab
        JPanel studentsPanel = new JPanel(new BorderLayout(10,10));

        //Table
        String[] studentColumns = {"Student ID", "Full Name", "Department", "Year", "Username"};
        DefaultTableModel studentTableModel = new DefaultTableModel(studentColumns,0){
            public boolean isCellEditable(int row, int col){return false;};
        };
        JTable studentTable = new JTable(studentTableModel);
        JScrollPane studentScroll = new JScrollPane(studentTable);

        //Populating The Table
        for(StudentProfile s : ds.students){
            studentTableModel.addRow(new Object[]{s.studentID, s.fullName, s.department, s.year, s.userName});
        }

        //Adding Student Form
        JPanel addStudentForm = new JPanel(new GridLayout(6, 2, 10, 10));
        addStudentForm.setBorder(BorderFactory.createTitledBorder("Add Student"));

        JTextField newStudentID = new JTextField();
        JTextField newStudentName = new JTextField();
        JTextField newDepartment = new JTextField();
        JTextField newYear = new JTextField();
        JComboBox<String> studentUserCombo = new JComboBox<>();
        for(User u : ds.users){
            if(u.role.equals("STUDENT")) studentUserCombo.addItem(u.username);
        }

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

        addStudentBtn.addActionListener(e-> {
            String sid = newStudentID.getText().trim();
            String sname = newStudentName.getText().trim();
            String dept = newDepartment.getText().trim();
            String yearStr= newYear.getText().trim();
            String suname = (String) studentUserCombo.getSelectedItem();

            if(sid.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please Enter your ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(sname.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please Enter your name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(dept.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please Enter your Department", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(yearStr.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please Enter The Year", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int year = Integer.parseInt(yearStr);
                ds.students.add(new StudentProfile(sid, sname, dept, year, suname));
                ds.saveStudents();
                studentTableModel.addRow(new Object[]{sid, sname, dept, year, suname});
                newStudentID.setText("");
                newStudentName.setText("");
                newDepartment.setText("");
                newYear.setText("");
                JOptionPane.showMessageDialog(this, "Student added!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Year must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        studentsPanel.add(studentScroll, BorderLayout.CENTER);
        studentsPanel.add(addStudentForm, BorderLayout.SOUTH);
        tabs.addTab("Students", studentsPanel);
        tabs.addTab("Courses", new JPanel());
        tabs.addTab("Reports", new JPanel());

        add(tabs);
    }

}