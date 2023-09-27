package net.codejava.jdbc;

import java.sql.*;
import java.util.Scanner;

public class studentcli {
    private static final int CREDIT_LIMIT = 7;

    public studentcli() {
    }

    //public static void main(String[] args) throws SQLException 
    public void run(Connection connection) throws SQLException {
    {
        /*Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db", "postgres", "1234");
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println("Unable to connect to database: " + e.getMessage());
            System.exit(1);
        }*/

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your Email ID:");
        String emailId = scanner.next();
        System.out.println("Enter your password:");
        String password = scanner.next();

        // Check if the entered credentials are valid
        if (isValidStudent(emailId, password)) {
            // Credentials are valid - allow access to the student interface
            System.out.println("Welcome Student!");
            boolean quit = false;
            while (!quit) {
            System.out.println("Enter 1 to view available courses");
            System.out.println("Enter 2 to register for a course");
            System.out.println("Enter 3 to deregister from a course");
            System.out.println("Enter 4 to view grades");
            System.out.println("Enter 5 to compute CGPA");
            System.out.println("Enter 6 to log out");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewAvailableCourses(connection);
                    break;
                case 2:
                    registerForCourse(connection, scanner, emailId);
                    break;
                case 3:
                    deregisterFromCourse(connection, scanner, emailId);
                    break;
                case 4:
                    viewGrades(connection, emailId);
                    break;
                case 5:
                    System.out.print("Enter semester number: ");
                    int semester = scanner.nextInt();
                    double cgpa = computeCGPA(connection, emailId, semester);
                    System.out.println("CGPA for semester " + semester + " is " + cgpa);
                    break;
                case 6:
                    System.out.println("Logged out");
                    quit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        try {
            connection.close();
            System.out.println("Connection closed.");
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
        scanner.close();
        }
    }
}
    // Check if the entered credentials are valid for a student
    private static boolean isValidStudent(String email_id, String password) {
        // Check the entered credentials against a database of valid student credentials
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db", "postgres", "1234")) {
            String sql = "SELECT * FROM authentication WHERE email_id = ?";
    
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email_id);
                ResultSet rs = stmt.executeQuery();
    
                if (rs.next()) {
                    // Student record found - compare the entered password against the stored password
                    String storedPassword = rs.getString("password");
                    return password.equals(storedPassword);
                } else {
                    // Student record not found - invalid username
                    return false;
                }
            }
        } catch (SQLException ex) {
            // Handle any database errors
            ex.printStackTrace();
            return false;
        }
        // Return true if the credentials are valid, false otherwise
        // return false;
        }
    private static void viewAvailableCourses(Connection connection) {
        try {
            String query = "SELECT coursecode, ltp, pre_req, credits FROM coursecatalog";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Available courses:");
            while (resultSet.next()) {
                String courseCode = resultSet.getString("coursecode");
                String ltp = resultSet.getString("ltp");
                String preReq = resultSet.getString("pre_req");
                int credits = resultSet.getInt("credits");
                System.out.printf("%s %s %s %d\n", courseCode, ltp, preReq, credits);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error retrieving available courses: " + e.getMessage());
        }
    }

private static void registerForCourse(Connection connection, Scanner scanner, String emailId) {
    System.out.print("Enter course code to register: ");
    String courseCode = scanner.nextLine();

    try {
        // Check if the student is already registered for the course
        String query = "SELECT * FROM registrations WHERE stu_email_id = ? AND course_code = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, emailId);
        statement.setString(2, courseCode);
        ResultSet resultSet = statement.executeQuery();
        boolean isRegistered = resultSet.next();

        if (isRegistered) {
            System.out.println("You are already registered for this course.");
        } else {
            // Check if the student has cleared the prerequisites for the course
            query = "SELECT * FROM coursecatalog WHERE coursecode = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, courseCode);
            resultSet = statement.executeQuery();
            boolean prerequisitesCleared = false;
            if (resultSet.next()) {
                String prerequisites = resultSet.getString("pre_req");
                if (prerequisites == null || prerequisites.isEmpty()) {
                    // If no prerequisites are specified, assume they have been cleared
                    prerequisitesCleared = true;
                } else {
                    // Check if the student has cleared all prerequisites
                    String[] prerequisiteList = prerequisites.split(",");
                    query = "SELECT * FROM registrations WHERE stu_email_id = ? AND course_code = ?";
                    for (String prerequisite : prerequisiteList) {
                        statement = connection.prepareStatement(query);
                        statement.setString(1, emailId);
                        statement.setString(2, prerequisite);
                        resultSet = statement.executeQuery();
                        if (!resultSet.next()) {
                            prerequisitesCleared = false;
                            break;
                        } else {
                            prerequisitesCleared = true;
                        }
                    }
                }
            }
            if (!prerequisitesCleared) {
                System.out.println("You have not cleared the prerequisites for this course.");
            } else {
                // Check if the student has exceeded the credit limit
                query = "SELECT SUM(credits) AS total_credits FROM registrations INNER JOIN coursecatalog ON registrations.course_code = coursecatalog.coursecode WHERE registrations.stu_email_id = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, emailId);
                resultSet = statement.executeQuery();
                int totalCredits = 0;
                if (resultSet.next()) {
                    totalCredits = resultSet.getInt("total_credits");
                }
                query = "SELECT MAX(credits) AS max_credits FROM coursecatalog WHERE coursecode = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, courseCode);
                resultSet = statement.executeQuery();
                int maxCredits = 0;
                if (resultSet.next()) {
                    maxCredits = resultSet.getInt("max_credits");
                }
                if (totalCredits + maxCredits > CREDIT_LIMIT) {
                    System.out.println("You have exceeded the maximum credit limit.");
                } else {
                    // Register the student for the course
                    query = "INSERT INTO registrations (stu_email_id, course_code) VALUES (?, ?)";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, emailId);
                    statement.setString(2, courseCode);
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected == 1) {
                        System.out.println("Successfully registered for course " + courseCode);
                    } else {
                        System.out.println("Registration failed. Please try again later.");
                    }
                }
            }
        }
    } catch (SQLException e) {
        System.out.println("Error occurred while registering for course: " + e.getMessage());
    }
}
    private static void deregisterFromCourse(Connection connection, Scanner scanner, String emailId) {
        System.out.print("Enter course code to deregister: ");
        String courseCode = scanner.nextLine();
    
        try {
            // Check if the student is registered for the course
            String query = "SELECT * FROM registrations WHERE stu_email_id = ? AND course_code = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, emailId);
            statement.setString(2, courseCode);
            ResultSet resultSet = statement.executeQuery();
            boolean isRegistered = resultSet.next();
    
            if (!isRegistered) {
                System.out.println("You are not registered for this course.");
            } else {
                // Deregister the student from the course
                query = "DELETE FROM registrations WHERE stu_email_id = ? AND course_code = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, emailId);
                statement.setString(2, courseCode);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 1) {
                    System.out.println("You have successfully deregistered from the course.");
                } else {
                    System.out.println("An error occurred while deregistering from the course.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void viewGrades(Connection connection, String emailId) {
        try {
            String query = "SELECT course_code, grade FROM record_of_grades WHERE email_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, emailId);
            ResultSet resultSet = statement.executeQuery();
    
            System.out.println("Grades for " + emailId + ":");
            while (resultSet.next()) {
                String courseCode = resultSet.getString("course_code");
                String grade = resultSet.getString("grade");
                System.out.println(courseCode + ": " + grade);
            }
    
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error retrieving grades: " + e.getMessage());
        }
    }
    public static double computeCGPA(Connection connection, String emailID, int semester) throws SQLException {
        double cgpa = 0.0;
        int totalCredits = 0;
        int totalGradePoints = 0;
        int previousSemester = semester - 1;
        if (previousSemester < 1) {
            // If this is the first semester, there is no previous CGPA to consider
            return calculateSGPA(connection, emailID, semester);
        }
        // Calculate the cumulative earned credits and grade points up to the previous semester
        String query = "SELECT course_code, grade FROM record_of_grades WHERE email_id = ? AND semester <= ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, emailID);
        statement.setInt(2, previousSemester);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String courseCode = resultSet.getString("course_code");
            String grade = resultSet.getString("grade");
            int credits = getCreditsForCourse(connection, courseCode);
            if (!grade.equals("NP") && !grade.equals("NF")) {
                double gradePoints = getGradePoints(grade);
                totalCredits += credits;
                totalGradePoints += (credits * gradePoints);
            }
        }
        resultSet.close();
        statement.close();
        // Add the earned credits and grade points from the current semester
        query = "SELECT course_code, grade FROM record_of_grades WHERE email_id = ? AND semester = ?";
        statement = connection.prepareStatement(query);
        statement.setString(1, emailID);
        statement.setInt(2, semester);
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String courseCode = resultSet.getString("course_code");
            String grade = resultSet.getString("grade");
            int credits = getCreditsForCourse(connection, courseCode);
            if (!grade.equals("NP") && !grade.equals("NF")) {
                double gradePoints = getGradePoints(grade);
                totalCredits += credits;
                totalGradePoints += (credits * gradePoints);
            }
        }
        resultSet.close();
        statement.close();
        // Compute the CGPA
        if (totalCredits > 0) {
            cgpa = (double) totalGradePoints / totalCredits;
        }
        return cgpa;
    }
    
    private static int getCreditsForCourse(Connection connection, String courseCode) throws SQLException {
        int credits = 0;
        String query = "SELECT credits FROM coursecatalog WHERE coursecode = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, courseCode);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            credits = resultSet.getInt("credits");
        }
        resultSet.close();
        statement.close();
        return credits;
    }
    private static double getGradePoints(String grade) {
        double gradePoints = 0.0;
        switch (grade) {
            case "A":
                gradePoints = 10.0;
                break;
            case "A-":
                gradePoints = 9.0;
                break;
            case "B":
                gradePoints = 8.0;
                break;
            case "B-":
                gradePoints = 7.0;
                break;
            case "C":
                gradePoints = 6.0;
                break;
            case "C-":
                gradePoints = 5.0;
                break;
            case "D":
                gradePoints = 4.0;
                break;
            case "E":
                gradePoints = 2.0;
                break;
            case "F":
                gradePoints = 0.0;
                break;
            default:
                // For other grades, return 0.0
                break;
        }
        return gradePoints;
    }
    public static double calculateSGPA(Connection connection, String studentID, int semester) throws SQLException {
        double sgpa = 0.0;
        int totalCredits = 0;
        int totalGradePoints = 0;
        String query = "SELECT course_code, grade FROM record_of_grades WHERE email_id = ? AND semester = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, studentID);
        statement.setInt(2, semester);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String courseCode = resultSet.getString("course_code");
            String grade = resultSet.getString("grade");
            int credits = getCreditsForCourse(connection, courseCode);
            if (!grade.equals("NP") && !grade.equals("NF")) {
                double gradePoints = getGradePoints(grade);
                totalCredits += credits;
                totalGradePoints += (credits * gradePoints);
            }
        }
        resultSet.close();
        statement.close();
        if (totalCredits > 0) {
            sgpa = (double) totalGradePoints / totalCredits;
        }
        return sgpa;
    }
    
}