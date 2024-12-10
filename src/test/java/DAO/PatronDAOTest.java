package DAO;

import DatabaseConnection.DatabaseUtil;
import Entity.Patron;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PatronDAOTest {

    @InjectMocks
    private PatronDAO patronDAO;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private Patron testPatron;

    @BeforeEach
    void setUp() throws SQLException {
        // Reset all mocks to ensure a clean state for each test
        Mockito.clearAllCaches();
        MockitoAnnotations.openMocks(this);

        testPatron = new Patron(1, "Jane", "Doe", "jane.doe@example.com", "123-456-7890", "123 Main St", LocalDate.of(2020, 1, 1));

        // Mock the static method for getting a connection (only once)
        mockStatic(DatabaseUtil.class);
        when(DatabaseUtil.getConnection()).thenReturn(mockConnection);

        // Reset mocks to ensure no leftover behavior
        reset(mockConnection, mockPreparedStatement, mockResultSet);
    }

    @Test
    void testGetPatronById_Success() throws SQLException {
        String query = "SELECT * FROM Patrons WHERE patron_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("patron_id")).thenReturn(1);
        when(mockResultSet.getString("first_name")).thenReturn("Jane");
        when(mockResultSet.getString("last_name")).thenReturn("Doe");
        when(mockResultSet.getString("email")).thenReturn("jane.doe@example.com");
        when(mockResultSet.getString("phone_number")).thenReturn("123-456-7890");
        when(mockResultSet.getString("address")).thenReturn("123 Main St");
        when(mockResultSet.getDate("membership_date")).thenReturn(Date.valueOf(LocalDate.of(2020, 1, 1)));

        Patron result = patronDAO.getPatronById(1);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals(1, result.getPatronId());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void testGetPatronById_NotFound() throws SQLException {
        String query = "SELECT * FROM Patrons WHERE patron_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Simulate no result found

        Patron result = patronDAO.getPatronById(testPatron.getPatronId());

        assertNull(result, "Patron should not be found with the given ID.");
    }

    @Test
    void testAddPatron_Success() throws SQLException {
        String query = "INSERT INTO Patrons (first_name, last_name, email, phone_number, address, membership_date) VALUES (?, ?, ?, ?, ?, ?)";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);

        patronDAO.addPatron(testPatron);

        verify(mockPreparedStatement, times(1)).setString(1, testPatron.getFirstName());
        verify(mockPreparedStatement, times(1)).setString(2, testPatron.getLastName());
        verify(mockPreparedStatement, times(1)).setString(3, testPatron.getEmail());
        verify(mockPreparedStatement, times(1)).setString(4, testPatron.getPhoneNumber());
        verify(mockPreparedStatement, times(1)).setString(5, testPatron.getAddress());
        verify(mockPreparedStatement, times(1)).setDate(6, Date.valueOf(testPatron.getMembershipDate()));
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetAllPatrons_Success() throws SQLException {
        String query = "SELECT * FROM Patrons";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // Simulate one result

        when(mockResultSet.getInt("patron_id")).thenReturn(testPatron.getPatronId());
        when(mockResultSet.getString("first_name")).thenReturn(testPatron.getFirstName());
        when(mockResultSet.getString("last_name")).thenReturn(testPatron.getLastName());
        when(mockResultSet.getString("email")).thenReturn(testPatron.getEmail());
        when(mockResultSet.getString("phone_number")).thenReturn(testPatron.getPhoneNumber());
        when(mockResultSet.getString("address")).thenReturn(testPatron.getAddress());
        when(mockResultSet.getDate("membership_date")).thenReturn(Date.valueOf(testPatron.getMembershipDate()));

        List<Patron> patrons = patronDAO.getAllPatrons();

        assertFalse(patrons.isEmpty(), "Patrons should be retrieved.");
        assertEquals(1, patrons.size(), "There should be one patron in the result.");
        assertEquals(testPatron.getFirstName(), patrons.get(0).getFirstName(), "The first name of the patron should match.");
    }

    @Test
    void testUpdatePatronAddress_Success() throws SQLException {
        String query = "UPDATE Patrons SET address = ? WHERE patron_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful update

        patronDAO.updatePatronAddress(testPatron.getPatronId(), "456 New St");

        verify(mockPreparedStatement, times(1)).setString(1, "456 New St");
        verify(mockPreparedStatement, times(1)).setInt(2, testPatron.getPatronId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeletePatron_Success() throws SQLException {
        String query = "DELETE FROM Patrons WHERE patron_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        patronDAO.deletePatron(testPatron.getPatronId());

        verify(mockPreparedStatement, times(1)).setInt(1, testPatron.getPatronId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testSearchPatronsByName_Success() throws SQLException {
        String query = "SELECT * FROM Patrons WHERE first_name LIKE ? OR last_name LIKE ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate that there is data in the ResultSet
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // One result, then end

        when(mockResultSet.getInt("patron_id")).thenReturn(testPatron.getPatronId());
        when(mockResultSet.getString("first_name")).thenReturn(testPatron.getFirstName());
        when(mockResultSet.getString("last_name")).thenReturn(testPatron.getLastName());
        when(mockResultSet.getString("email")).thenReturn(testPatron.getEmail());
        when(mockResultSet.getString("phone_number")).thenReturn(testPatron.getPhoneNumber());
        when(mockResultSet.getString("address")).thenReturn(testPatron.getAddress());
        when(mockResultSet.getDate("membership_date")).thenReturn(Date.valueOf(testPatron.getMembershipDate()));

        var patrons = patronDAO.searchPatronsByName("Jane");

        assertFalse(patrons.isEmpty(), "Patrons should be found when searched with the keyword.");
        assertEquals("Jane", patrons.get(0).getFirstName(), "The patron's first name should match the search result.");
    }
}
