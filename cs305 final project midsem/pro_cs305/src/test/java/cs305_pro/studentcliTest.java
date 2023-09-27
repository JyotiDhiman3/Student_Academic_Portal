package cs305_pro;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;
import java.io.*;
import java.sql.*;
import static org.junit.Assert.*;

public class studentcliTest {
    
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "1234";

    private static Connection connection;

    @BeforeClass
    public static void setUp() throws Exception {
        // Connect to the database
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        // Close the database connection
        connection.close();
    }

    @Test
    public void testViewAvailableCourses() throws Exception {
        //Create a test course catalog in the database
        /*String createQuery = "CREATE TABLE coursecatalog ("
            + "coursecode VARCHAR(5) PRIMARY KEY,"
            + "ltp TEXT,"
            + "pre_req TEXT,"
            + "credits INT)";
        PreparedStatement createStatement = connection.prepareStatement(createQuery);
        createStatement.executeUpdate();*/

        // Insert some test courses
        /*String insertQuery = "INSERT INTO coursecatalog (coursecode, ltp, pre_req, credits) VALUES (?, ?, ?, ?)";
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.setString(1, "EE111");
        insertStatement.setString(2, "3-0-0");
        insertStatement.setString(3, "NIL");
        insertStatement.setInt(4, 3);
        insertStatement.executeUpdate();

        insertStatement.setString(1, "CH111");
        insertStatement.setString(2, "3-0-0");
        insertStatement.setString(3, "NIL");
        insertStatement.setInt(4, 3);
        insertStatement.executeUpdate();
        */
        // Test the viewAvailableCourses method
        String expectedOutput = "Available courses:\r\n" +
                "CS305 4-1-0 NIL 4\n" +
                "CS507 4-1-0 CS305 4\n" +
                "BM403 3-0-0 NIL 3\n" +
                "GE404 2-0-2 NIL 3\n" +
                "CS401 3-0-0 CS303 3\n" +
                "CS303 3-1-2 NIL 4\n" +
                "MA101 3-0-2 NIL 4\n" +
                "MA201 3-0-0 NIL 3\n" +
                "MA301 3-1-2 MA101,MA201 4\n" +
                "HS402 2-0-0 NIL 2\n"; 
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        studentcli.viewAvailableCourses(connection);
        String actualOutput = baos.toString();
        assertEquals(expectedOutput, actualOutput);

        // Clean up the test course catalog
        /*String dropQuery = "DROP TABLE coursecatalog";
        PreparedStatement dropStatement = connection.prepareStatement(dropQuery);
        dropStatement.executeUpdate();*/
    }

    @Test
    public void testIsValidStudent() {
        studentcli studentcli = new studentcli();

        // Test with valid credentials
        assertTrue(studentcli.isValidStudent("2020csb1092@iitrpr.ac.in", "2020csb1092"));

        // Test with invalid credentials
        assertFalse(studentcli.isValidStudent("2020csb@iitrpr.ac.in", "2020csb"));

        // Test with invalid email
        assertFalse(studentcli.isValidStudent("2020csbb1092@iitrpr.ac.in", "2020csb1092"));

        // Test with null password
        assertFalse(studentcli.isValidStudent("2020csb1092@iitrpr.ac.in", null));
    }
}
