package cs305_pro;
//package net.codejava.jdbc;

import java.io.*;
import java.sql.*;
import java.util.*;

public class acadofficecli {
    private static Connection connection;

    public void initializeConnection() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/db",
                    "postgres",
                    "1234");
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }

    public void run(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome!");
        while (true) {
            System.out.println("Press 1 to edit course catalog");
            System.out.println("Press 2 to view grades of all students");
            System.out.println("Press 3 to generate transcript of students");
            System.out.println("Press 4 to logout");
            int option = scanner.nextInt();
            switch (option) {
                case 1:
                    editCourseCatalog();
                    break;
                case 2:
                    viewGradesOfAllStudents(connection);
                    break;
                case 3:
                    generateTranscript();
                    break;
                case 4:
                    System.out.println("Logged out");
                    return;
                default:
                    System.out.println("Invalid option selected");
                    break;
            }
        }
    }

    public void editCourseCatalog() {
        Scanner scanner = new Scanner(System.in);
        
        // Authenticate the user
        if (!authenticateUser(scanner)) {
        System.out.println("Invalid username or password");
        return;
        }
    
        // If authenticated, allow the user to edit the course catalog
        while (true) {
            System.out.println("Press 1 to add a course");
            System.out.println("Press 2 to delete a course");
            System.out.println("Press 3 to update a course");
            System.out.println("Press 4 to exit");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume the newline character
    
            switch (option) {
                case 1:
                    // Add a course to the course catalog
                    System.out.println("Enter the course code:");
                    String courseCode = scanner.nextLine();
                    System.out.println("Enter the L-T-P value:");
                    String ltp = scanner.nextLine();
                    System.out.println("Enter the prerequisites:");
                    String prerequisites = scanner.nextLine();
                    System.out.println("Enter the number of credits:");
                    int credits = scanner.nextInt();
                    scanner.nextLine(); // consume the newline character
    
                    try {
                        PreparedStatement statement = connection.prepareStatement("INSERT INTO coursecatalog (coursecode, ltp, pre_req, credits) VALUES (?, ?, ?, ?)");
                        statement.setString(1, courseCode);
                        statement.setString(2, ltp);
                        statement.setString(3, prerequisites);
                        statement.setInt(4, credits);
                        statement.executeUpdate();
                        statement.close();
                        System.out.println("Course added successfully");
                    } catch (SQLException e) {
                        System.out.println("Error adding course: " + e.getMessage());
                    }
    
                    break;
                case 2:
                    // Delete a course from the course catalog
                    System.out.println("Enter the course code:");
                    courseCode = scanner.nextLine();
    
                    try {
                        PreparedStatement statement = connection.prepareStatement("DELETE FROM coursecatalog WHERE coursecode = ?");
                        statement.setString(1, courseCode);
                        int numRowsDeleted = statement.executeUpdate();
                        statement.close();
    
                        if (numRowsDeleted > 0) {
                            System.out.println("Course deleted successfully");
                        } else {
                            System.out.println("No such course found");
                        }
                    } catch (SQLException e) {
                        System.out.println("Error deleting course: " + e.getMessage());
                    }
    
                    break;
                case 3:
                // Update a course in the course catalog
                System.out.println("Enter the course code:");
                String course_Code = scanner.nextLine();

                try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM coursecatalog WHERE coursecode = ?");
                statement.setString(1, course_Code);
                ResultSet rs = statement.executeQuery();
                boolean courseExists = rs.next();
                rs.close();
                statement.close();

                if (!courseExists) {
                System.out.println("No such course found.");
                break;
                }

    System.out.println("Enter the new L-T-P value:");
    String l_t_p = scanner.nextLine();

    System.out.println("Enter the new pre-requisites:");
    String pre_req = scanner.nextLine();

    System.out.println("Enter the new credits:");
    int credits_ = scanner.nextInt();

    statement = connection.prepareStatement("UPDATE coursecatalog SET ltp = ?, pre_req = ?, credits = ? WHERE coursecode = ?");
    statement.setString(1, l_t_p);
    statement.setString(2, pre_req);
    statement.setInt(3, credits_);
    statement.setString(4, course_Code);
    int rowsUpdated = statement.executeUpdate();
    statement.close();

    if (rowsUpdated > 0) {
        System.out.println("Course updated successfully.");
    } else {
        System.out.println("Failed to update course.");
    }

} catch (SQLException e) {
    System.out.println("Error occurred while updating course: " + e.getMessage());
}
               case 4:
               return;
}
}
}
    private boolean authenticateUser(Scanner scanner) {
        System.out.println("Enter your username:");
        String username = scanner.nextLine();
        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM acad_auth WHERE username = ?");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (storedPassword.equals(password)) {
                    rs.close();
                    statement.close();
                    return true;
                }
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error checking authentication: " + e.getMessage());
        }

        return false;
    }

    static void viewGradesOfAllStudents(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM record_of_grades");
            ResultSet rs = statement.executeQuery();

            System.out.printf("%s %s %s %s %s\n", "Name", "Email ID", "Course Code", "Semester", "Grade");
            while (rs.next()) {
                String emailId = rs.getString("email_id");
                String courseCode = rs.getString("course_code");
                int semester = rs.getInt("semester");
                String grade = rs.getString("grade");

                PreparedStatement userStatement = connection.prepareStatement("SELECT name FROM record_of_grades WHERE email_id = ?");
                userStatement.setString(1, emailId);
                ResultSet userRs = userStatement.executeQuery();
                userRs.next();
                String name = userRs.getString("name");
                // String lastName = userRs.getString("last_name");
                // String name = firstName + " " + lastName;

                System.out.printf("%s %s %s %s %s\n", name, emailId, courseCode, semester, grade);
            }
        } catch (SQLException e) {
            System.out.println("Error viewing grades: " + e.getMessage());
        }
    }

    static void generateTranscript() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the email ID of the student:");
        String emailId = scanner.nextLine();

        try {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT course_code, grade, semester FROM record_of_grades WHERE email_id = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement.setString(1, emailId);
            ResultSet rs = statement.executeQuery();

            // Check if the student has any grades
            if (!rs.isBeforeFirst()) {
                System.out.println("No grades found for student with Email ID " + emailId);
                return;
            }

            // Print the header of the transcript
            System.out.println("Transcript for student " + emailId + ":");
            System.out.printf("%-15s %-10s %-15s%n", "Course code", "Grade", "Semester");
            System.out.println("----------------------------------");

            // Print the grades
            while (rs.next()) {
                String courseCode = rs.getString("course_code");
                String grade = rs.getString("grade");
                int semester = rs.getInt("semester");
                System.out.printf("%-15s %-10s %-15s%n", courseCode, grade, semester);
            }

            // Save the transcript to a file
            String fileName = emailId + ".txt";
            PrintWriter writer = new PrintWriter(fileName);
            writer.println("Transcript for student " + emailId + ":");
            writer.printf("%-15s %-10s %-15s%n", "Course code", "Grade", "Semester");
            writer.println("----------------------------------");
            rs.beforeFirst(); // Move the cursor to the beginning of the result set
            while (rs.next()) {
                String courseCode = rs.getString("course_code");
                String grade = rs.getString("grade");
                int semester = rs.getInt("semester");
                writer.printf("%-15s %-10s %-15s%n", courseCode, grade, semester);
            }
            writer.close();

            System.out.println("Transcript saved to file " + fileName);
        } catch (SQLException | FileNotFoundException e) {
            System.out.println("Error generating transcript: " + e.getMessage());
        }
    }
}

