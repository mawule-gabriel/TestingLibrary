package Entity;

import Entity.Enums.ReservationStatus;
import DAO.PatronDAO;
import DAO.BookDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationTest {

    private Reservation reservation;
    private PatronDAO patronDAO;
    private BookDAO bookDAO;

    @BeforeEach
    public void setUp() {
        // Create the necessary mock objects for PatronDAO and BookDAO
        patronDAO = mock(PatronDAO.class);
        bookDAO = mock(BookDAO.class);

        // Create a new Reservation instance for each test
        reservation = new Reservation(
                1,
                1,
                101,
                LocalDate.of(2024, 12, 1),
                ReservationStatus.PENDING,
                LocalDate.of(2024, 12, 15)
        );
    }

    @Test
    public void testConstructor() {
        // Test constructor and check initial values
        assertEquals(1, reservation.getReservationId());
        assertEquals(1, reservation.getPatronId());
        assertEquals(101, reservation.getBookId());
        assertEquals(LocalDate.of(2024, 12, 1), reservation.getReservationDate());
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        assertEquals(LocalDate.of(2024, 12, 15), reservation.getDueDate());
    }

    @Test
    public void testSettersAndGetters() {
        // Test setters and getters individually
        reservation.setReservationId(2);
        reservation.setPatronId(2);
        reservation.setBookId(102);
        reservation.setReservationDate(LocalDate.of(2024, 12, 5));
        reservation.setStatus(ReservationStatus.FULFILLED);
        reservation.setDueDate(LocalDate.of(2024, 12, 20));

        assertEquals(2, reservation.getReservationId());
        assertEquals(2, reservation.getPatronId());
        assertEquals(102, reservation.getBookId());
        assertEquals(LocalDate.of(2024, 12, 5), reservation.getReservationDate());
        assertEquals(ReservationStatus.FULFILLED, reservation.getStatus());
        assertEquals(LocalDate.of(2024, 12, 20), reservation.getDueDate());
    }

    @Test
    public void testToString() {
        // Test the toString method for correct output format
        String expected = "Reservation{" +
                "reservationId=" + 1 +
                ", patronId=" + 1 +
                ", bookId=" + 101 +
                ", reservationDate=" + LocalDate.of(2024, 12, 1) +
                ", status=" + ReservationStatus.PENDING +
                ", dueDate=" + LocalDate.of(2024, 12, 15) +
                '}';
        assertEquals(expected, reservation.toString());
    }

}
