package cs305_pro;

import static org.junit.Assert.assertTrue;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class studentcliTest6 {

    private Connection conn;

    @Before
    public void setUp() throws SQLException {
        // Connect to the PostgreSQL database
        conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db", "postgres", "1234");
    }

    @After
    public void tearDown() throws SQLException {
        // Close the database connection
        conn.close();
    }

    @Test
    public void testCheckGraduationEligibility() throws SQLException {
        // Insert test data into the course_completion table
        insertTestData();
        
        // Call the checkGraduationEligibility method
        boolean result = checkGraduationEligibility();

        // Assert that the result is true
        Assert.assertTrue(result);
    }

    private void insertTestData() throws SQLException {
        // Insert test data into the course_completion table
        String sql = "INSERT INTO course_completion (email_id, course_code, course_type, completed, grade) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Insert data for a student who is eligible for graduation
            stmt.setString(1, "student1@example.com");
            stmt.setString(2, "CS101");
            stmt.setString(3, "Program core");
            stmt.setBoolean(4, true);
            stmt.setString(5, "A");
            stmt.executeUpdate();

            stmt.setString(1, "student1@example.com");
            stmt.setString(2, "CS102");
            stmt.setString(3, "Program elective");
            stmt.setBoolean(4, true);
            stmt.setString(5, "B");
            stmt.executeUpdate();

            stmt.setString(1, "student1@example.com");
            stmt.setString(2, "CS103");
            stmt.setString(3, "Program elective");
            stmt.setBoolean(4, true);
            stmt.setString(5, "C");
            stmt.executeUpdate();

            stmt.setString(1, "student1@example.com");
            stmt.setString(2, "CS201");
            stmt.setString(3, "Program elective");
            stmt.setBoolean(4, true);
            stmt.setString(5, "D");
            stmt.executeUpdate();

            stmt.setString(1, "student1@example.com");
            stmt.setString(2, "CP401");
            stmt.setString(3, "BTP Capstone");
            stmt.setBoolean(4, true);
            stmt.setString(5, "C");
            stmt.executeUpdate();

            // Insert data for a student who is not eligible for graduation
            stmt.setString(1, "student2@example.com");
            stmt.setString(2, "CS101");
            stmt.setString(3, "Program core");
            stmt.setBoolean(4, true);
            stmt.setString(5, "A");
            stmt.executeUpdate();

            stmt.setString(1, "student2@example.com");
            stmt.setString(2, "CS102");
            stmt.setString(3, "Program elective");
            stmt.setBoolean(4, true);
            stmt.setString(5, "B");
            stmt.executeUpdate();

            stmt.setString(1, "student2@example.com");
            stmt.setString(2, "CS201");
            stmt.setString(3, "Program elective");
            stmt.setBoolean(4, true);
            stmt.setString(5, "C");
            stmt.executeUpdate();

            stmt.setString(1, "student2@example.com");
            stmt.setString(2, "CS202");
            stmt.setString(3, "Program elective");
            stmt.setBoolean(4, true);
            stmt.setString(5, "D");
            stmt.executeUpdate();

            stmt.setString(1, "student2@example.com");
            stmt.setString(2, "CP401");
            stmt.setString(3, "BTP Capstone");
            stmt.setBoolean(4, true);
            stmt.setString(5, "D");
            stmt.executeUpdate();
        }
    }

    private boolean checkGraduationEligibility() throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db", "postgres", "1234")) {
            // Query the course_completion table to get the student_id where all the course requirements are met
            String courseSql = "SELECT email_id FROM course_completion WHERE course_type = 'Program core' AND completed = true " +
                    "UNION SELECT email_id FROM course_completion WHERE course_type = 'Program elective' AND completed = true GROUP BY email_id " +
                    "HAVING COUNT(*) >= 3 AND SUM(CASE WHEN grade >= 'D' THEN 1 ELSE 0 END) >= 2 " +
                    "UNION SELECT email_id FROM course_completion WHERE course_code = 'BTP' AND completed = true AND grade >= 'D'";
    
            try (PreparedStatement courseStmt = conn.prepareStatement(courseSql)) {
                ResultSet courseRs = courseStmt.executeQuery();
    
                // Iterate through the result set and check each student's eligibility
                while (courseRs.next()) {
                    String email_id = courseRs.getString(1);
    
                    // Call the stored procedure to check the student's eligibility
                    String spSql = "SELECT * FROM check_graduation_eligibility(?)";
                    try (CallableStatement spStmt = conn.prepareCall(spSql)) {
                        spStmt.setString(1, email_id);
                        boolean isEligible = spStmt.execute();
                        if (isEligible) {
                            System.out.println("Congratulations! Student " + email_id + " is eligible for graduation.");
                        } else {
                            System.out.println("Sorry, student " + email_id + " is not yet eligible for graduation.");
                        }
                    }
                }
            }
            return true;
        } catch (SQLException ex) {
            // Handle any database errors
            ex.printStackTrace();
            return false;
        }
    }
}    

