package cs305_pro;

import java.io.*;
import java.sql.*;
import java.util.*;

public class facultycli {

    public void run(Connection connection) throws SQLException {
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your Email ID:");
            String emailId = scanner.next();
            System.out.println("Enter your password:");
            String password = scanner.next();
    
            // Check if the entered credentials are valid
            if (isValidFaculty(emailId, password)) {
                // Credentials are valid - allow access to the student interface
                System.out.println("Welcome!");
                boolean quit = false;
                while (!quit) {
                System.out.println("Press 1 to view grades of all the students in your (specific) course");
                System.out.println("Press 2 to register a course you like to offer");
                System.out.println("Press 3 to deregister a course you offered");
                System.out.println("Press 4 to update your course grades");
                System.out.println("Press 5 to logout");
    
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();
    
                switch (choice) {
                    case 1:
                        viewGradesOfAllStudents(connection);
                        break;
                    case 2:
                        registerForOfferedCourse(connection, scanner, emailId);
                        break;
                    case 3:
                        deregisterFromOfferedCourse(connection, scanner, emailId);
                        break;
                    case 4:
                        System.out.print("Enter the filepath: ");
                        String filePath = scanner.nextLine();
                        updateCourseGradesFromFile(connection, filePath);
                        break;
                    case 5:
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
    static void viewGradesOfAllStudents(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the course code: ");
        String courseCode = scanner.nextLine();
    
        String sql = "SELECT email_id, name, grade FROM record_of_grades WHERE course_code = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, courseCode);
            ResultSet resultSet = statement.executeQuery();
    
            System.out.println("Grades for course " + courseCode + ":");
            System.out.println("========================================================================================================");
            System.out.printf("%-30s%-40s%-15s\n", "Email ID", "Name", "Grade");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            while (resultSet.next()) {
                String emailId = resultSet.getString("email_id");
                String name = resultSet.getString("name");
                String grade = resultSet.getString("grade");
                System.out.printf("%-30s%-40s%-15s\n", emailId, name, grade);
            }
            System.out.println("--------------------------------------------------------------------------------------------------------");
        }
    }        
    
    // Check if the entered credentials are valid for faculty
    static boolean isValidFaculty(String email_id, String password) {
            // Check the entered credentials against a database of valid faculty credentials
            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db", "postgres", "1234")) {
                String sql = "SELECT * FROM auth_instructor WHERE instructor_id = ?";
        
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
    // Register a course to be offered by the instructor
    private static void registerForOfferedCourse(Connection connection, Scanner scanner, String emailId) throws SQLException {
        System.out.print("Enter the course code you would like to offer: ");
        String courseCode = scanner.nextLine();
        System.out.print("Enter the CGPA criteria for this course: ");
        String cgpaCriteria = scanner.nextLine();
        System.out.print("Enter the semester number: ");
        int semester = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter the course type (e.g. program core, program elective, btp capstone): ");
        String courseType = scanner.nextLine();
        System.out.print("Enter the year for which the course is being offered (e.g. 2020 batch, all years): ");
        String yearForCourseOffered = scanner.nextLine();
    
        // Check if the course code exists in the course catalog
        if (!isCourseCodeValid(connection, courseCode)) {
            System.out.println("Invalid course code. Please try again.");
            return;
        }
    
        // Check if the instructor is already offering the selected course in the same semester
        if (isOfferingCourseInSemester(connection, emailId, courseCode, semester)) {
            System.out.println("You are already offering the selected course in this semester.");
            return;
        }
    
        // Insert the new course offering into the instructor table
        String sql = "INSERT INTO instructor (instructor_id, course_code_offered, cgpa_criteria, sem, course_type, year_for_course_offered) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, emailId);
            stmt.setString(2, courseCode);
            stmt.setString(3, cgpaCriteria);
            stmt.setInt(4, semester);
            stmt.setString(5, courseType);
            stmt.setString(6, yearForCourseOffered);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Course offering registered successfully.");
            } else {
                System.out.println("Course offering registration failed. Please try again.");
            }
        }
    }
    
// Deregister a course being offered by the instructor
static void deregisterFromOfferedCourse(Connection connection, Scanner scanner, String emailId) throws SQLException {
    System.out.print("Enter the course code you would like to deregister: ");
    String courseCode = scanner.nextLine();
    System.out.print("Enter the semester number: ");
    int semester = scanner.nextInt();
    scanner.nextLine();

    // Check if the instructor is offering the selected course in the specified semester
    if (!isOfferingCourseInSemester(connection, emailId, courseCode, semester)) {
        System.out.println("You are not offering the selected course in the specified semester.");
        return;
    }

    // Remove the course offering from the instructor table
    String sql = "DELETE FROM instructor WHERE instructor_id = ? AND course_code_offered = ? AND sem = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, emailId);
        stmt.setString(2, courseCode);
        stmt.setInt(3, semester);
        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected == 1) {
            System.out.println("Course offering deregistered successfully.");
        } else {
            System.out.println("Course offering deregistration failed. Please try again.");
        }
    }
}

// Check if a course code exists in the course catalog
private static boolean isCourseCodeValid(Connection connection, String courseCode) throws SQLException {
    String sql = "SELECT * FROM coursecatalog WHERE coursecode = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, courseCode);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }
}

// Check if the instructor is offering a course in a specific semester
private static boolean isOfferingCourseInSemester(Connection connection, String emailId, String courseCode, int semester) throws SQLException {
    String sql = "SELECT * FROM instructor WHERE instructor_id = ? AND course_code_offered = ? AND sem = ?";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, emailId);
    statement.setString(2, courseCode);
    statement.setInt(3, semester);
    ResultSet result = statement.executeQuery();
    return result.next();
}
static void updateCourseGradesFromFile(Connection connection, String filePath) throws SQLException {
    try {
        // Read the contents of the CSV file
        List<String[]> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            lines.add(values);
        }
        reader.close();

        // Update the grades in the database
        PreparedStatement statement = connection.prepareStatement("UPDATE record_of_grades SET grade = ? WHERE email_id = ? AND course_code = ? AND semester = ?");
        for (String[] values : lines) {
            String emailId = values[0];
            String courseCode = values[1];
            int semester = Integer.parseInt(values[2]);
            String grade = values[3];
            statement.setString(1, grade);
            statement.setString(2, emailId);
            statement.setString(3, courseCode);
            statement.setInt(4, semester);
            statement.executeUpdate();
        }
        statement.close();

        System.out.println("Grades updated successfully");
    } catch (IOException e) {
        System.out.println("Error reading file: " + e.getMessage());
    }
}

//This implementation assumes that the CSV file has four columns: 
//email_id, course_code, semester, and grade, in that order. 
//It reads the contents of the file into a list of arrays, where
//each array represents a row of the file. Then, it loops through
//the list and updates the record_of_grades table in the database for each row. 
}





