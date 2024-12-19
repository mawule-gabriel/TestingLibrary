package Service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import DAO.TransactionDAO;
import Entity.Book;
import Entity.Transaction;
import Entity.Enums.BookStatus;
import Entity.Enums.TransactionType;
import Exception.BookException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionDAO transactionDAO;

    @Mock
    private BookService bookService;

    private TransactionService transactionService;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() throws Exception {
        // Create actual TransactionService instance
        transactionService = new TransactionService();

        // Use reflection to set the mocked dependencies
        Field transactionDAOField = TransactionService.class.getDeclaredField("transactionDAO");
        transactionDAOField.setAccessible(true);
        transactionDAOField.set(transactionService, transactionDAO);

        Field bookServiceField = TransactionService.class.getDeclaredField("bookService");
        bookServiceField.setAccessible(true);
        bookServiceField.set(transactionService, bookService);

        // Create a sample transaction for testing
        testTransaction = new Transaction(
                1,
                101,
                201,
                LocalDate.now().minusDays(7),
                null,
                LocalDate.now().plusDays(14),
                BigDecimal.ZERO,
                TransactionType.BORROW
        );
    }

    @Test
    void testAddTransaction_Success() throws SQLException, BookException {
        // Arrange
        doNothing().when(transactionDAO).addTransaction(any(Transaction.class));
        doNothing().when(bookService).updateBookStatus(anyInt(), any(BookStatus.class));

        // Act
        transactionService.addTransaction(testTransaction);

        // Assert
        verify(transactionDAO).addTransaction(testTransaction);
        verify(bookService).updateBookStatus(testTransaction.getBookId(), BookStatus.BORROWED);
    }

    @Test
    void testAddTransaction_InvalidPatronId() {
        // Arrange
        Transaction invalidTransaction = new Transaction(
                1,
                0,  // Invalid patron ID
                201,
                LocalDate.now().minusDays(7),
                null,
                LocalDate.now().plusDays(14),
                BigDecimal.ZERO,
                TransactionType.BORROW
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.addTransaction(invalidTransaction));

        assertTrue(exception.getMessage().contains("Invalid patron ID"));
    }

    @Test
    void testGetAllTransactions_Success() throws SQLException {
        // Arrange
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(testTransaction);
        transactionList.add(new Transaction(
                2,
                102,
                202,
                LocalDate.now().minusDays(10),
                null,
                LocalDate.now().plusDays(7),
                BigDecimal.ZERO,
                TransactionType.BORROW
        ));

        when(transactionDAO.getAllTransactions()).thenReturn(transactionList);

        // Act
        List<Transaction> resultTransactions = transactionService.getAllTransactions();

        // Assert
        assertNotNull(resultTransactions);
        assertEquals(2, resultTransactions.size());
        verify(transactionDAO).getAllTransactions();
    }

    @Test
    void testDeleteTransaction_Success() throws SQLException {
        // Arrange
        doNothing().when(transactionDAO).deleteTransaction(anyInt());

        // Act
        transactionService.deleteTransaction(1);

        // Assert
        verify(transactionDAO).deleteTransaction(1);
    }

    @Test
    void testDeleteTransaction_InvalidId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.deleteTransaction(0));

        assertTrue(exception.getMessage().contains("Transaction ID must be greater than zero"));
    }

    @Test
    void testBorrowBook_Success() throws SQLException, BookException {
        // Arrange
        int patronId = 101;
        int bookId = 201;
        doNothing().when(transactionDAO).addTransaction(any(Transaction.class));
        doNothing().when(bookService).updateBookStatus(anyInt(), any(BookStatus.class));

        // Act
        transactionService.borrowBook(patronId, bookId);

        // Assert
        verify(transactionDAO).addTransaction(any(Transaction.class));
        verify(bookService).updateBookStatus(bookId, BookStatus.BORROWED);
    }

    @Test
    void testReturnBook_Success() throws SQLException, BookException {
        // Arrange
        int transactionId = 1;
        when(transactionDAO.getTransactionById(transactionId)).thenReturn(testTransaction);
        doNothing().when(transactionDAO).addTransaction(any(Transaction.class));
        doNothing().when(bookService).updateBookStatus(anyInt(), any(BookStatus.class));

        // Act
        transactionService.returnBook(transactionId);

        // Assert
        verify(transactionDAO).getTransactionById(transactionId);
        verify(transactionDAO).addTransaction(any(Transaction.class));
        verify(bookService).updateBookStatus(testTransaction.getBookId(), BookStatus.AVAILABLE);
    }

    @Test
    void testReturnBook_LateReturn() throws SQLException, BookException {
        // Arrange
        int transactionId = 1;
        Transaction lateTransaction = new Transaction(
                1,
                101,
                201,
                LocalDate.now().minusDays(21),
                null,
                LocalDate.now().minusDays(7),
                BigDecimal.ZERO,
                TransactionType.BORROW
        );
        when(transactionDAO.getTransactionById(transactionId)).thenReturn(lateTransaction);
        doNothing().when(transactionDAO).addTransaction(any(Transaction.class));
        doNothing().when(bookService).updateBookStatus(anyInt(), any(BookStatus.class));

        // Act
        transactionService.returnBook(transactionId);

        // Assert
        verify(transactionDAO).getTransactionById(transactionId);
        verify(transactionDAO).addTransaction(argThat(transaction ->
                transaction.getFine().compareTo(BigDecimal.ZERO) > 0 &&
                        transaction.getTransactionType() == TransactionType.RETURN
        ));
        verify(bookService).updateBookStatus(lateTransaction.getBookId(), BookStatus.AVAILABLE);
    }

    @Test
    void testReturnBook_TransactionNotFound() throws SQLException {
        // Arrange
        int transactionId = 999;
        when(transactionDAO.getTransactionById(transactionId)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.returnBook(transactionId));

        assertTrue(exception.getMessage().contains("Transaction not found"));
    }
}