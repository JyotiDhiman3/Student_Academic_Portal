package cs305_pro;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.sql.*;
import java.util.Scanner;

@RunWith(JUnit4.class)
public class studentcliTest2 {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "1234";

    private static Connection connection;
    private static Scanner scanner;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        // Set up the test database
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE coursecatalog (coursecode TEXT PRIMARY KEY, ltp TEXT, pre_req TEXT, credits INTEGER)");
        statement.executeUpdate("CREATE TABLE registrations (stu_email_id TEXT, course_code TEXT, PRIMARY KEY (stu_email_id, course_code))");

        // Insert some sample data
        statement.executeUpdate("INSERT INTO coursecatalog (coursecode, ltp, pre_req, credits) VALUES ('CS101', '3-0-0', 'NIL', 3)");
        statement.executeUpdate("INSERT INTO coursecatalog (coursecode, ltp, pre_req, credits) VALUES ('CS102', '3-0-0', 'NIL', 3)");
        statement.executeUpdate("INSERT INTO coursecatalog (coursecode, ltp, pre_req, credits) VALUES ('CS201', '3-0-0', 'CS101', 3)");
        statement.executeUpdate("INSERT INTO coursecatalog (coursecode, ltp, pre_req, credits) VALUES ('CS202', '3-0-0', 'CS101,CS102', 3)");

        // Initialize the scanner
        scanner = new Scanner(System.in);
    }

    @AfterClass
    public static void tearDownClass() throws SQLException {
        // Clean up the test database
        Statement statement = connection.createStatement();
        statement.executeUpdate("DROP TABLE registrations");
        statement.executeUpdate("DROP TABLE coursecatalog");
        connection.close();
    }

    @Test
    public void testRegisterForCourse() throws SQLException {
        // Set up the test data
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO registrations (stu_email_id, course_code) VALUES ('test@example.com', 'CS101')");

        // Run the method being tested
        studentcli.registerForCourse(connection, scanner, "test@example.com");

        // Verify the results
        ResultSet resultSet = statement.executeQuery("SELECT * FROM registrations WHERE stu_email_id = 'test@example.com' AND course_code = 'CS101'");
        Assert.assertTrue("Registration should not have been inserted again.", resultSet.next());
        Assert.assertFalse("There should be no additional registrations.", resultSet.next());

        resultSet = statement.executeQuery("SELECT * FROM registrations WHERE stu_email_id = 'test@example.com' AND course_code = 'CS201'");
        Assert.assertFalse("Student should not be registered for course with prerequisites not met.", resultSet.next());

        resultSet = statement.executeQuery("SELECT * FROM registrations WHERE stu_email_id = 'test@example.com' AND course_code = 'CS202'");
        Assert.assertFalse("Student should not be registered for course with prerequisites not met.", resultSet.next());

        resultSet = statement.executeQuery("SELECT SUM(credits) AS total_credits FROM registrations INNER JOIN coursecatalog ON registrations.course_code = coursecatalog.coursecode WHERE registrations.stu_email_id = 'test@example.com'");
        Assert.assertTrue("Total credits should be updated.", resultSet.next());
        Assert.assertEquals("Total credits should be updated.", 3, resultSet.getInt("total_credits"));
    }
}