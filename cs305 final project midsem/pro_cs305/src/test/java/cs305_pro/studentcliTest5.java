package cs305_pro;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class studentcliTest5 {
    private static Connection conn;

    @BeforeAll
    public static void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db", "postgres", "1234");
    }

    @Test
    public void testUpdateStudentProfile() throws SQLException {
    conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db", "postgres", "1234");
    // Create a new student profile to update
    String emailId = "example@example.com";
    PreparedStatement ps = conn.prepareStatement("INSERT INTO student_profile (email_id, contact_details, batch, joining_date) VALUES (?, ?, ?, ?)");
    ps.setString(1, emailId);
    ps.setString(2, "123-456-7890");
    ps.setString(3, "Fall 2022");
    ps.setString(4, "2022-09-01");
    ps.executeUpdate();

    // Set up input for the update
    Scanner scanner = new Scanner("111-222-3333\nSpring 2023\n2023-03-07\n");

    // Call the method being tested
    studentcli.updateStudentProfile(conn, scanner, emailId);

    // Verify that the profile was updated correctly
    ps = conn.prepareStatement("SELECT * FROM student_profile WHERE email_id = ?");
    ps.setString(1, emailId);
    ResultSet rs = ps.executeQuery();
    rs.next();
    if (Objects.equals(rs.getString("contact_details"), "111-222-3333")) {
        System.out.println("Contact details updated successfully.");
    } else {
        System.out.println("Error updating contact details.");
    }
    // Verify that the batch was updated correctly
    if (Objects.equals(rs.getString("batch"), "Spring 2023")) {
    System.out.println("Batch updated successfully.");
    } else {
    System.out.println("Error updating batch.");
    }

    // Verify that the joining date was updated correctly
    if (Objects.equals(rs.getString("joining_date"), "2023-03-07")) {
    System.out.println("Joining date updated successfully.");
    } else {
    System.out.println("Error updating joining date.");
    }

    // Clean up the test data
    ps = conn.prepareStatement("DELETE FROM student_profile WHERE email_id = ?");
    ps.setString(1, emailId);
    ps.executeUpdate();
}

}
