package Controller;

import Controller.ReservationController;
import Entity.Enums.ReservationStatus;
import Entity.Reservation;
import Service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationControllerTest {

    private ReservationController reservationController;
    private ReservationService reservationService;
    private Reservation reservation;

    @BeforeEach
    public void setUp() {
        // Initialize the mock service and controller
        reservationService = mock(ReservationService.class);
        reservationController = new ReservationController(reservationService);  // Inject the mocked service directly

        // Correctly initialize a test Reservation
        reservation = new Reservation(1, 1, 1, LocalDate.now(), ReservationStatus.PENDING, LocalDate.now().plusDays(7));
    }

    @Test
    public void testAddReservation_Success() throws SQLException {
        // Arrange: mock the behavior of reservationService.addReservation
        doNothing().when(reservationService).addReservation(reservation);

        // Act: Call the method
        reservationController.addReservation(reservation);

        // Assert: Verify that the addReservation method was called
        verify(reservationService, times(1)).addReservation(reservation);
    }


    @Test
    public void testGetAllReservations_Success() throws SQLException {
        // Arrange: Create a list of mock reservations
        List<Reservation> reservations = Arrays.asList(
                new Reservation(1, 1, 1, LocalDate.now(), ReservationStatus.PENDING, LocalDate.now().plusDays(7)),
                new Reservation(2, 2, 2, LocalDate.now(), ReservationStatus.FULFILLED, LocalDate.now().plusDays(7))
        );

        when(reservationService.getAllReservations()).thenReturn(reservations);

        // Act: Call the method
        List<Reservation> result = reservationController.getAllReservations();

        // Assert: Verify that the correct reservations are returned
        assertEquals(2, result.size());
        assertTrue(result.containsAll(reservations));
    }

    @Test
    public void testGetAllReservations_Empty() throws SQLException {
        // Arrange: Mock the empty list
        when(reservationService.getAllReservations()).thenReturn(Arrays.asList());

        // Act: Call the method
        List<Reservation> result = reservationController.getAllReservations();

        // Assert: Verify that the returned list is empty
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteReservation_Success() throws SQLException {
        // Arrange: Mock the behavior of deleting a reservation
        doNothing().when(reservationService).deleteReservation(1);

        // Act: Call the deleteReservation method
        reservationController.deleteReservation(1);

        // Assert: Verify that the deleteReservation method was called
        verify(reservationService, times(1)).deleteReservation(1);
    }


}
