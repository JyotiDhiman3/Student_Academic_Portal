package cs305_pro;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;

@RunWith(JUnit4.class)
public class studentcliTest3{

    private static Connection connection;
    private static Scanner scanner;

    @BeforeClass
    public static void setup() throws SQLException {
        // Create a connection to the database
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db", "postgres", "1234");

        // Create a scanner to read input from the console
        InputStream input = new ByteArrayInputStream("test".getBytes());
        scanner = new Scanner(input);
    }

    @AfterClass
    public static void teardown() throws SQLException {
        // Close the connection to the database
        connection.close();
    }

    @Test
    public void testDeregisterFromCourse() throws SQLException {
        // Set up the test data
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO registrations (stu_email_id, course_code) VALUES ('test@example.com', 'CS101')");

        // Run the method being tested
        studentcli.deregisterFromCourse(connection, scanner, "test@example.com");

        // Verify the results
        ResultSet resultSet = statement.executeQuery("SELECT * FROM registrations WHERE stu_email_id = 'test@example.com' AND course_code = 'CS101'");
        Assert.assertFalse("Registration should have been deleted.", resultSet.next());
    }

    @Test
    public void testViewGrades() throws SQLException {
        // Set up the test data
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO record_of_grades (email_id, course_code, grade) VALUES ('test@example.com', 'CS101', 'A')");

        // Run the method being tested
        studentcli.viewGrades(connection, "test@example.com");

        // Verify the results
        ResultSet resultSet = statement.executeQuery("SELECT * FROM record_of_grades WHERE email_id = 'test@example.com' AND course_code = 'CS101'");
        Assert.assertTrue("Student grade should have been retrieved.", resultSet.next());
        Assert.assertEquals("Student grade should have been retrieved.", "A", resultSet.getString("grade"));
    }
}
