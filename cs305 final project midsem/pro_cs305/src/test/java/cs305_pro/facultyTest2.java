package cs305_pro;

import static org.junit.Assert.assertEquals;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class facultyTest2 {
    @Test
    public void testIsValidFacultyValidCredentials() {
        // Set up the test data
        String email = "valid.email@domain.com";
        String password = "valid_password";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Insert valid faculty credentials into the database
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db", "postgres", "1234")) {
            String insertSql = "INSERT INTO auth_instructor (instructor_id, password) VALUES (?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setString(1, email);
                stmt.setString(2, hashedPassword);
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Call the method with the valid credentials and check the result
        boolean result = facultycli.isValidFaculty(email, password);
        Assertions.assertThat(result);
    }

    @Test
    public void testIsValidFacultyInvalidCredentials() {
        // Set up the test data
        String email = "invalid.email@domain.com";
        String password = "invalid_password";

        // Call the method with the invalid credentials and check the result
        boolean result = facultycli.isValidFaculty(email, password);
        Assertions.assertThat(result);
    }

    // Rest of your test methods here
}
