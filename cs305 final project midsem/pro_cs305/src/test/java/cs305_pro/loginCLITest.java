package cs305_pro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;

public class loginCLITest {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/db";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "1234";

    @Test
    public void testStudentLogin() throws SQLException {
        String input = "1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        studentcli cli = new studentcli();
    }

    @Test
    public void testFacultyLogin() throws SQLException {
        String input = "2\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        facultycli cli = new facultycli();
    }

    @Test
    public void testAcadOfficeLogin() throws SQLException {
        String input = "3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        acadofficecli cli = new acadofficecli();
        cli.initializeConnection();
    }

}
