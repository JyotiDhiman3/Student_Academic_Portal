package cs305_pro;

import static org.junit.Assert.assertEquals;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class studentcliTest4 {

    private static Connection connection;

    @BeforeClass
    public static void setUp() throws SQLException {
        // Set up a connection to a test database
        connection = DriverManager.getConnection("jdbc:postgresql://localhost/db", "postgres", "1234");
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        // Close the connection after all tests have run
        connection.close();
    }

    @Test
    public void testComputeCGPA() throws SQLException {
        // Test the computeCGPA method for a specific email ID and semester
        String emailID = "johndoe@example.com";
        int semester = 3;
        double expectedCGPA = 0.01;
        double actualCGPA = studentcli.computeCGPA(connection, emailID, semester);
        assertEquals(expectedCGPA, actualCGPA, 0.01);
    }

    

}


