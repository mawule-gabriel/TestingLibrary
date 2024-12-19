package DAO;

import DatabaseConnection.DatabaseUtil;
import Entity.Enums.ReservationStatus;
import Entity.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ReservationDAOTest {

    @InjectMocks
    private ReservationDAO reservationDAO;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private Reservation testReservation;

    @BeforeEach
    void setUp() throws SQLException {
        // Reset all mocks to ensure a clean state for each test
        Mockito.clearAllCaches();
        MockitoAnnotations.openMocks(this);

        testReservation = new Reservation(
                1,  // reservationId
                101,  // patronId
                1001,  // bookId
                LocalDate.of(2024, 12, 16),  // reservationDate
                ReservationStatus.PENDING,  // status
                LocalDate.of(2025, 12, 16)  // dueDate
        );

        // Mock the static method for getting a connection (only once)
        mockStatic(DatabaseUtil.class);
        when(DatabaseUtil.getConnection()).thenReturn(mockConnection);

        // Reset mocks to ensure no leftover behavior
        reset(mockConnection, mockPreparedStatement, mockResultSet);
    }

    @Test
    void testAddReservation_Success() throws SQLException {
        String query = "INSERT INTO Reservations (patron_id, book_id, reservation_date, status, due_date) VALUES (?, ?, ?, ?, ?)";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);

        reservationDAO.addReservation(testReservation);

        verify(mockPreparedStatement, times(1)).setInt(1, testReservation.getPatronId());
        verify(mockPreparedStatement, times(1)).setInt(2, testReservation.getBookId());
        verify(mockPreparedStatement, times(1)).setDate(3, Date.valueOf(testReservation.getReservationDate()));
        verify(mockPreparedStatement, times(1)).setString(4, testReservation.getStatus().name());
        verify(mockPreparedStatement, times(1)).setDate(5, Date.valueOf(testReservation.getDueDate()));
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetReservationById_Success() throws SQLException {
        String query = "SELECT * FROM Reservations WHERE reservation_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("reservation_id")).thenReturn(1);
        when(mockResultSet.getInt("patron_id")).thenReturn(101);
        when(mockResultSet.getInt("book_id")).thenReturn(1001);
        when(mockResultSet.getDate("reservation_date")).thenReturn(Date.valueOf("2024-12-16"));
        when(mockResultSet.getString("status")).thenReturn(ReservationStatus.PENDING.name());
        when(mockResultSet.getDate("due_date")).thenReturn(Date.valueOf("2025-12-16"));

        Reservation result = reservationDAO.getReservationById(1);

        assertNotNull(result);
        assertEquals(1, result.getReservationId());
        assertEquals(101, result.getPatronId());
        assertEquals(ReservationStatus.PENDING, result.getStatus());
        assertEquals(LocalDate.of(2025, 12, 16), result.getDueDate());
    }

    @Test
    void testGetReservationById_NotFound() throws SQLException {
        String query = "SELECT * FROM Reservations WHERE reservation_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Simulate no result found

        Reservation result = reservationDAO.getReservationById(999);

        assertNull(result, "Reservation should not be found with the given ID.");
    }

    @Test
    void testGetAllReservations_Success() throws SQLException {
        String query = "SELECT * FROM Reservations";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);  // Simulate one result

        when(mockResultSet.getInt("reservation_id")).thenReturn(testReservation.getReservationId());
        when(mockResultSet.getInt("patron_id")).thenReturn(testReservation.getPatronId());
        when(mockResultSet.getInt("book_id")).thenReturn(testReservation.getBookId());
        when(mockResultSet.getDate("reservation_date")).thenReturn(Date.valueOf(testReservation.getReservationDate()));
        when(mockResultSet.getString("status")).thenReturn(testReservation.getStatus().name());
        when(mockResultSet.getDate("due_date")).thenReturn(Date.valueOf(testReservation.getDueDate()));

        List<Reservation> reservations = reservationDAO.getAllReservations();

        assertFalse(reservations.isEmpty(), "Reservations should be retrieved.");
        assertEquals(1, reservations.size(), "There should be one reservation in the result.");
        assertEquals(testReservation.getReservationId(), reservations.get(0).getReservationId(), "The reservation ID should match.");
    }

    @Test
    void testUpdateReservationStatus_Success() throws SQLException {
        String query = "UPDATE Reservations SET status = ? WHERE reservation_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful update

        reservationDAO.updateReservationStatus(testReservation.getReservationId(), ReservationStatus.PENDING);

        verify(mockPreparedStatement, times(1)).setString(1, ReservationStatus.PENDING.name());
        verify(mockPreparedStatement, times(1)).setInt(2, testReservation.getReservationId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateReservationStatus_Exception() throws SQLException {
        String query = "UPDATE Reservations SET status = ? WHERE reservation_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        doThrow(new SQLException("Database error")).when(mockPreparedStatement).executeUpdate();

        assertThrows(SQLException.class, () -> {
            reservationDAO.updateReservationStatus(testReservation.getReservationId(), ReservationStatus.CANCELLED);
        });
    }

    @Test
    void testDeleteReservation_Success() throws SQLException {
        String query = "DELETE FROM Reservations WHERE reservation_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);  // Simulate successful deletion

        reservationDAO.deleteReservation(testReservation.getReservationId());

        verify(mockPreparedStatement, times(1)).setInt(1, testReservation.getReservationId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteReservation_ForeignKeyConstraintViolation() throws SQLException {
        String query = "DELETE FROM Reservations WHERE reservation_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        doThrow(new SQLIntegrityConstraintViolationException("Foreign key constraint fails"))
                .when(mockPreparedStatement).executeUpdate();

        assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            reservationDAO.deleteReservation(testReservation.getReservationId());
        });
    }

    @Test
    void testGetReservationById_Exception() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class, () -> {
            reservationDAO.getReservationById(testReservation.getReservationId());
        });
    }
}
