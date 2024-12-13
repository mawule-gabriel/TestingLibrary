package Entity;

import Entity.Enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        // Create a new Transaction instance before each test
        transaction = new Transaction(
                1,
                1,
                101,
                LocalDate.of(2024, 12, 1),
                LocalDate.of(2024, 12, 10),
                LocalDate.of(2024, 12, 15),
                new BigDecimal("2.50"),
                TransactionType.BORROW
        );
    }

    @Test
    public void testConstructor() {
        // Test constructor and check initial values
        assertEquals(1, transaction.getTransactionId());
        assertEquals(1, transaction.getPatronId());
        assertEquals(101, transaction.getBookId());
        assertEquals(LocalDate.of(2024, 12, 1), transaction.getBorrowDate());
        assertEquals(LocalDate.of(2024, 12, 10), transaction.getReturnDate());
        assertEquals(LocalDate.of(2024, 12, 15), transaction.getDueDate());
        assertEquals(new BigDecimal("2.50"), transaction.getFine());
        assertEquals(TransactionType.BORROW, transaction.getTransactionType());
    }

    @Test
    public void testSettersAndGetters() {
        // Test setters and getters individually
        transaction.setTransactionId(2);
        transaction.setPatronId(2);
        transaction.setBookId(102);
        transaction.setBorrowDate(LocalDate.of(2024, 12, 5));
        transaction.setReturnDate(LocalDate.of(2024, 12, 12));
        transaction.setDueDate(LocalDate.of(2024, 12, 17));
        transaction.setFine(new BigDecimal("3.00"));
        transaction.setTransactionType(TransactionType.RETURN);

        assertEquals(2, transaction.getTransactionId());
        assertEquals(2, transaction.getPatronId());
        assertEquals(102, transaction.getBookId());
        assertEquals(LocalDate.of(2024, 12, 5), transaction.getBorrowDate());
        assertEquals(LocalDate.of(2024, 12, 12), transaction.getReturnDate());
        assertEquals(LocalDate.of(2024, 12, 17), transaction.getDueDate());
        assertEquals(new BigDecimal("3.00"), transaction.getFine());
        assertEquals(TransactionType.RETURN, transaction.getTransactionType());
    }

    @Test
    public void testToString() {
        // Test the toString method for correct output format
        String expected = "Transaction{" +
                "transactionId=" + 1 +
                ", patronId=" + 1 +
                ", bookId=" + 101 +
                ", borrowDate=" + LocalDate.of(2024, 12, 1) +
                ", returnDate=" + LocalDate.of(2024, 12, 10) +
                ", dueDate=" + LocalDate.of(2024, 12, 15) +
                ", fine=" + new BigDecimal("2.50") +
                ", transactionType=" + TransactionType.BORROW +
                '}';
        assertEquals(expected, transaction.toString());
    }
}
