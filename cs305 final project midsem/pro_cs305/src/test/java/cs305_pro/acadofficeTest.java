package cs305_pro;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class acadofficeTest {
    private Connection mockConnection;
    private PreparedStatement mockStatement1;
    private PreparedStatement mockStatement2;
    private ResultSet mockResultSet1;
    private ResultSet mockResultSet2;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Before
    public void setup() throws SQLException {
        mockConnection = mock(Connection.class);
        mockStatement1 = mock(PreparedStatement.class);
        mockStatement2 = mock(PreparedStatement.class);
        mockResultSet1 = mock(ResultSet.class);
        mockResultSet2 = mock(ResultSet.class);
        
        // Set up mocks for the first query
        when(mockConnection.prepareStatement("SELECT * FROM record_of_grades")).thenReturn(mockStatement1);
        when(mockStatement1.executeQuery()).thenReturn(mockResultSet1);
        when(mockResultSet1.next()).thenReturn(true, true, false);
        when(mockResultSet1.getString("email_id")).thenReturn("student1@example.com", "student2@example.com");
        when(mockResultSet1.getString("course_code")).thenReturn("CS305", "CS101");
        when(mockResultSet1.getInt("semester")).thenReturn(2, 1);
        when(mockResultSet1.getString("grade")).thenReturn("A", "B");

        // Set up mocks for the second query
        when(mockConnection.prepareStatement("SELECT name FROM record_of_grades WHERE email_id = ?")).thenReturn(mockStatement2);
        when(mockStatement2.executeQuery()).thenReturn(mockResultSet2);
        when(mockResultSet2.next()).thenReturn(true, true, false);
        when(mockResultSet2.getString("name")).thenReturn("Alice", "Bob");
    }

    @Test
    public void testViewGradesOfAllStudents() throws SQLException {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        // Call the method to be tested
        acadofficecli.viewGradesOfAllStudents(mockConnection);

        // Verify that the queries were executed as expected
        verify(mockConnection).prepareStatement("SELECT * FROM record_of_grades");
        verify(mockStatement1).executeQuery();
        verify(mockResultSet1, times(2)).getString("email_id");
        verify(mockResultSet1, times(2)).getString("course_code");
        verify(mockResultSet1, times(2)).getInt("semester");
        verify(mockResultSet1, times(2)).getString("grade");
        verify(mockResultSet1, times(2)).next();

        verify(mockConnection).prepareStatement("SELECT name FROM record_of_grades WHERE email_id = ?");
        verify(mockStatement2, times(2)).setString(1, "student1@example.com");
        verify(mockStatement2, times(2)).setString(1, "student2@example.com");
        verify(mockStatement2, times(2)).executeQuery();
        verify(mockResultSet2, times(2)).getString("name");
        verify(mockResultSet2, times(2)).next();
        
        // Verify that the result set was iterated through twice
        verify(mockResultSet, times(2)).next();
        // Verify that the output is correct
        String expectedOutput = "Name Email ID Course Code Semester Grade\n" +
                                "Alice student1@example.com CS305 2 A\n" +
                                "Bob student2@example.com CS101 1 B\n";
        assertEquals(expectedOutput, outContent.toString());
    }
}
