package cs305_pro;

import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
public class facultyTest4 {

    @Test
    public void testUpdateCourseGradesFromFile() throws SQLException, IOException {
        // Connect to the PostgreSQL database for testing
        Connection connection;
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db", "postgres", "1234");
        // Create a temporary file with test data
        String testCsvData = "test@example.com,CS101,1,A\n" +
                             "test@example.com,CS102,2,B\n";
        File tempFile = createTempCsvFile(testCsvData);
    
        // Call the method being tested
        facultycli.updateCourseGradesFromFile(connection, tempFile.getAbsolutePath());
    
        // Verify that the grades were updated correctly
        PreparedStatement statement = connection.prepareStatement("SELECT grade FROM record_of_grades WHERE email_id = ? AND course_code = ? AND semester = ?");
        statement.setString(1, "test@example.com");
        statement.setString(2, "CS101");
        statement.setInt(3, 1);
        ResultSet rs = statement.executeQuery();
        rs.next();
        assertEquals("A", rs.getString("grade"));
    
        statement.setString(1, "test@example.com");
        statement.setString(2, "CS102");
        statement.setInt(3, 2);
        rs = statement.executeQuery();
        rs.next();
        assertEquals("B", rs.getString("grade"));
    
        // Clean up the temporary file
        tempFile.delete();
    }
    
    private File createTempCsvFile(String csvData) throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        FileWriter writer = new FileWriter(tempFile);
        writer.write(csvData);
        writer.close();
        return tempFile;
    }
    
}
