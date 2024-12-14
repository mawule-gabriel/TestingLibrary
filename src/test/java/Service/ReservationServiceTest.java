package Service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import DAO.ReservationDAO;
import Entity.Reservation;
import Entity.Enums.ReservationStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationDAO reservationDAO;

    private ReservationService reservationService;
    private Reservation testReservation;

    @BeforeEach
    void setUp() throws Exception {
        // Create actual ReservationService instance
        reservationService = new ReservationService();

        // Use reflection to set the reservationDAO field
        Field reservationDAOField = ReservationService.class.getDeclaredField("reservationDAO");
        reservationDAOField.setAccessible(true);
        reservationDAOField.set(reservationService, reservationDAO);

        // Create a valid reservation for testing
        testReservation = new Reservation(
                1,
                101,
                201,
                LocalDate.now(),
                ReservationStatus.PENDING,
                LocalDate.now().plusDays(14)
        );
    }

    @Test
    void testAddReservation_Success() throws SQLException {
        // Arrange
        doNothing().when(reservationDAO).addReservation(any(Reservation.class));

        // Act
        reservationService.addReservation(testReservation);

        // Assert
        verify(reservationDAO).addReservation(testReservation);
    }

    @Test
    void testAddReservation_InvalidPatronId() {
        // Arrange
        Reservation invalidReservation = new Reservation(
                1,
                0,  // Invalid patron ID
                201,
                LocalDate.now(),
                ReservationStatus.PENDING,
                LocalDate.now().plusDays(14)
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.addReservation(invalidReservation));

        assertTrue(exception.getMessage().contains("Invalid Patron ID"));
    }

    @Test
    void testAddReservation_InvalidBookId() {
        // Arrange
        Reservation invalidReservation = new Reservation(
                1,
                101,
                0,  // Invalid book ID
                LocalDate.now(),
                ReservationStatus.PENDING,
                LocalDate.now().plusDays(14)
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.addReservation(invalidReservation));

        assertTrue(exception.getMessage().contains("Invalid Book ID"));
    }

    @Test
    void testAddReservation_NullReservationDate() {
        // Arrange
        Reservation invalidReservation = new Reservation(
                1,
                101,
                201,
                null,  // Null reservation date
                ReservationStatus.PENDING,
                LocalDate.now().plusDays(14)
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.addReservation(invalidReservation));

        assertTrue(exception.getMessage().contains("Reservation date cannot be null"));
    }

    @Test
    void testAddReservation_InvalidDueDate() {
        // Arrange
        Reservation invalidReservation = new Reservation(
                1,
                101,
                201,
                LocalDate.now(),
                ReservationStatus.PENDING,
                LocalDate.now().minusDays(14)  // Due date before reservation date
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.addReservation(invalidReservation));

        assertTrue(exception.getMessage().contains("Due date cannot be before the reservation date"));
    }

    @Test
    void testGetAllReservations_Success() throws SQLException {
        // Arrange
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(testReservation);
        reservationList.add(new Reservation(
                2,
                102,
                202,
                LocalDate.now().minusDays(5),
                ReservationStatus.FULFILLED,
                LocalDate.now().plusDays(7)
        ));

        when(reservationDAO.getAllReservations()).thenReturn(reservationList);

        // Act
        List<Reservation> resultReservations = reservationService.getAllReservations();

        // Assert
        assertNotNull(resultReservations);
        assertEquals(2, resultReservations.size());
        verify(reservationDAO).getAllReservations();
    }

    @Test
    void testDeleteReservation_Success() throws SQLException {
        // Arrange
        int reservationId = 1;
        when(reservationDAO.getReservationById(reservationId)).thenReturn(testReservation);
        doNothing().when(reservationDAO).deleteReservation(reservationId);

        // Act
        reservationService.deleteReservation(reservationId);

        // Assert
        verify(reservationDAO).getReservationById(reservationId);
        verify(reservationDAO).deleteReservation(reservationId);
    }

    @Test
    void testDeleteReservation_NotFound() throws SQLException {
        // Arrange
        int reservationId = 999;
        when(reservationDAO.getReservationById(reservationId)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.deleteReservation(reservationId));

        assertTrue(exception.getMessage().contains("Cannot delete. No reservation found with ID"));
    }
}