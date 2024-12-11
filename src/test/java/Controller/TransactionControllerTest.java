package Controller;

import Entity.Transaction;
import Entity.Enums.TransactionType;
import Service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionControllerTest {

    private TransactionController transactionController;
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Mock dependencies
        transactionService = mock(TransactionService.class);

        // Instantiate the TransactionController using the default constructor
        transactionController = new TransactionController();

        // Inject the mock TransactionService into the controller using reflection
        Field field = TransactionController.class.getDeclaredField("transactionService");
        field.setAccessible(true); // Make the field accessible
        field.set(transactionController, transactionService); // Inject the mock into the controller
    }

    // Test for adding a transaction with a validation error
    @Test
    public void testAddTransaction_Failure_ValidationError() {
        // Arrange: Simulate invalid transaction (e.g., invalid patronId)
        Transaction invalidTransaction = new Transaction(
                0, // invalid patronId
                123, // patronId
                456, // bookId
                LocalDate.now(), // borrowDate
                null, // returnDate (not set yet)
                LocalDate.now().plusWeeks(2), // dueDate
                BigDecimal.ZERO, // fine
                TransactionType.BORROW // transactionType
        );

        // Simulate validation error in the service layer
        doThrow(new IllegalArgumentException("Invalid patron ID.")).when(transactionService).addTransaction(any(Transaction.class));

        // Act & Assert: Verify that IllegalArgumentException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionController.addTransaction(invalidTransaction);
        });

        // Assert: Check if the exception message matches
        assertEquals("Invalid patron ID.", exception.getMessage());
    }

    // Test for adding a transaction successfully
    @Test
    public void testAddTransaction_Success() {
        // Arrange: Simulate successful transaction addition
        Transaction validTransaction = new Transaction(
                1, // transactionId
                123, // patronId
                456, // bookId
                LocalDate.now(), // borrowDate
                null, // returnDate (not set yet)
                LocalDate.now().plusWeeks(2), // dueDate
                BigDecimal.ZERO, // fine
                TransactionType.BORROW // transactionType
        );

        doNothing().when(transactionService).addTransaction(any(Transaction.class));

        // Act: Call addTransaction method
        transactionController.addTransaction(validTransaction);

        // Assert: Verify that addTransaction was called once on the service
        verify(transactionService, times(1)).addTransaction(validTransaction);
    }

    @Test
    public void testGetAllTransactions() {
        // Arrange: Prepare mock transaction list
        List<Transaction> transactions = List.of(
                new Transaction(1, 123, 456, LocalDate.now(), null, LocalDate.now().plusWeeks(2), BigDecimal.ZERO, TransactionType.BORROW)
        );
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        // Act: Call the method
        List<Transaction> result = transactionController.getAllTransactions();

        // Assert: Verify result
        assertEquals(1, result.size());
        assertEquals(123, result.get(0).getPatronId());
    }

    @Test
    public void testDeleteTransaction_Success() {
        // Arrange: Mock successful delete
        doNothing().when(transactionService).deleteTransaction(anyInt());

        // Act: Call the delete method
        transactionController.deleteTransaction(1);

        // Assert: Verify that deleteTransaction was called with correct ID
        verify(transactionService, times(1)).deleteTransaction(1);
    }

    @Test
    public void testBorrowBook_Success() {
        // Arrange: Mock successful borrow
        doNothing().when(transactionService).borrowBook(anyInt(), anyInt());

        // Act: Call the borrowBook method
        transactionController.borrowBook(1, 456);

        // Assert: Verify that borrowBook was called with correct parameters
        verify(transactionService, times(1)).borrowBook(1, 456);
    }

    @Test
    public void testReturnBook_Success() {
        // Arrange: Mock successful return
        doNothing().when(transactionService).returnBook(anyInt());

        // Act: Call the returnBook method
        transactionController.returnBook(1);

        // Assert: Verify that returnBook was called with correct transaction ID
        verify(transactionService, times(1)).returnBook(1);
    }



}
