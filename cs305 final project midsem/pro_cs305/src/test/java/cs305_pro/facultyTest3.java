package cs305_pro;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class facultyTest3 {
    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db", "postgres", "1234");
        connection.setAutoCommit(false);
    }

    @Test
    public void testDeregisterFromOfferedCourse_Successful() throws SQLException {
        // Set up test data
        String instructorEmail = "johndoe@example.com";
        String courseCode = "CS101";
        int semester = 1;

        // Add the course offering to the instructor table
        addCourseOffering(connection, instructorEmail, courseCode, semester);

        // Call the method being tested
        facultycli.deregisterFromOfferedCourse(connection, new Scanner(System.in), instructorEmail);

        // Verify the expected output
        assertFalse(isOfferingCourseInSemester(connection, instructorEmail, courseCode, semester));
    }

    @Test
    public void testDeregisterFromOfferedCourse_NotOfferingCourseInSemester() throws SQLException {
        // Set up test data
        String instructorEmail = "johndoe@example.com";
        String courseCode = "CS101";
        int semester = 1;

        // Call the method being tested
        facultycli.deregisterFromOfferedCourse(connection, new Scanner(System.in), instructorEmail);

        // Verify the expected output
        assertTrue(isOfferingCourseInSemester(connection, instructorEmail, courseCode, semester));
    }

    private void addCourseOffering(Connection connection, String instructorEmail, String courseCode, int semester) throws SQLException {
        String sql = "INSERT INTO instructor (instructor_id, course_code_offered, sem) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, instructorEmail);
            stmt.setString(2, courseCode);
            stmt.setInt(3, semester);
            stmt.executeUpdate();
            connection.commit();
        }
    }

    private boolean isOfferingCourseInSemester(Connection connection, String instructorEmail, String courseCode, int semester) throws SQLException {
        String sql = "SELECT COUNT(*) FROM instructor WHERE instructor_id = ? AND course_code_offered = ? AND sem = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, instructorEmail);
            stmt.setString(2, courseCode);
            stmt.setInt(3, semester);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            return count == 1;
        }
    }
}
/*This test case sets up a test database connection, adds a
 course offering to the instructor table using the addCourseOffering method,
 and then calls the deregisterFromOfferedCourse method with the appropriate
 arguments. After the method call, the test checks whether the course offering
 was removed from the instructor table using the isOfferingCourseInSemester 
 method. The test case also includes a separate test method for the scenario
 where the instructor is not offering the selected course in the specified semester. */