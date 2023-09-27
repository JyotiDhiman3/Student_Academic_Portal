package cs305_pro;

import org.junit.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class acadofficeTest2 {

    private Connection connection;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Before
    public void setUp() throws SQLException {
        // Connect to the test database
        String url = "jdbc:postgresql://localhost/db";
        String user = "postgres";
        String password = "1234";
        connection = DriverManager.getConnection(url, user, password);

        // Redirect System.out to outputStream for testing
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    public void testGenerateTranscript() throws SQLException, IOException {
        // Prepare test data
        String emailId = "test_student@example.com";
        String expectedOutput = "Transcript for student test_student@example.com:\n";
        expectedOutput += "Course code     Grade      Semester       \n";
        expectedOutput += "------------------------------------------\n";
        expectedOutput += "CS101         A          1              \n";
        expectedOutput += "CS102         B+         2              \n";
        expectedOutput += "Transcript saved to file test_student@example.com.txt\n";

        // Insert test data into the database
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE record_of_grades (email_id VARCHAR(100), course_code VARCHAR(10), grade VARCHAR(3), semester INT)");
        statement.executeUpdate("INSERT INTO record_of_grades (email_id, course_code, grade, semester) VALUES " +
                "('test_student@example.com', 'CS101', 'A', 1), " +
                "('test_student@example.com', 'CS102', 'B+', 2)");

        // Generate the transcript
        TranscriptGenerator generator = new TranscriptGenerator();
        generator.generateTranscript(emailId);

        // Verify the transcript was generated correctly
        assertEquals(expectedOutput, outputStream.toString());

        // Verify the transcript was saved to file
        File file = new File(emailId + ".txt");
        assertTrue(file.exists());
        String fileContent = Files.readString(Paths.get(file.getAbsolutePath()));
        assertEquals(expectedOutput, fileContent);

        // Clean up test data
        statement.executeUpdate("DROP TABLE record_of_grades");
        file.delete();
    }

    @After
    public void tearDown() throws SQLException {
        // Disconnect from the database
        connection.close();

        // Restore System.out
        System.setOut(System.out);
    }
}

