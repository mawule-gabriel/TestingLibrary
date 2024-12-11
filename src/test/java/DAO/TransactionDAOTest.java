package DAO;

import DatabaseConnection.DatabaseUtil;
import Entity.Transaction;
import Entity.Enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TransactionDAOTest {

    @InjectMocks
    private TransactionDAO transactionDAO;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @Mock
    private Statement mockStatement;

    private Transaction testTransaction;

    @BeforeEach
    void setUp() throws SQLException {
        Mockito.clearAllCaches();
        MockitoAnnotations.openMocks(this);

        testTransaction = new Transaction(1, 1, 1, LocalDate.now(), null, LocalDate.now().plusWeeks(2), BigDecimal.ZERO, TransactionType.BORROW);

        mockStatic(DatabaseUtil.class);
        when(DatabaseUtil.getConnection()).thenReturn(mockConnection);

        reset(mockConnection, mockPreparedStatement, mockResultSet);
    }

    @Test
    void testAddTransaction_Success() throws SQLException {
        String query = "INSERT INTO Transactions (patron_id, book_id, borrow_date, return_date, due_date, fine, transaction_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);

        transactionDAO.addTransaction(testTransaction);

        verify(mockPreparedStatement, times(1)).setInt(1, testTransaction.getPatronId());
        verify(mockPreparedStatement, times(1)).setInt(2, testTransaction.getBookId());
        verify(mockPreparedStatement, times(1)).setDate(3, Date.valueOf(testTransaction.getBorrowDate()));
        verify(mockPreparedStatement, times(1)).setDate(4, null); // No return date
        verify(mockPreparedStatement, times(1)).setDate(5, Date.valueOf(testTransaction.getDueDate()));
        verify(mockPreparedStatement, times(1)).setBigDecimal(6, testTransaction.getFine());
        verify(mockPreparedStatement, times(1)).setString(7, testTransaction.getTransactionType().name());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetTransactionById_Success() throws SQLException {
        String query = "SELECT * FROM Transactions WHERE transaction_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("transaction_id")).thenReturn(1);
        when(mockResultSet.getInt("patron_id")).thenReturn(1);
        when(mockResultSet.getInt("book_id")).thenReturn(1);
        when(mockResultSet.getDate("borrow_date")).thenReturn(Date.valueOf(testTransaction.getBorrowDate()));
        when(mockResultSet.getDate("due_date")).thenReturn(Date.valueOf(testTransaction.getDueDate()));
        when(mockResultSet.getBigDecimal("fine")).thenReturn(BigDecimal.ZERO);
        when(mockResultSet.getString("transaction_type")).thenReturn("BORROW");

        Transaction result = transactionDAO.getTransactionById(1);

        assertNotNull(result);
        assertEquals(testTransaction.getTransactionId(), result.getTransactionId());
        assertEquals(testTransaction.getPatronId(), result.getPatronId());
    }

    @Test
    void testGetTransactionById_NotFound() throws SQLException {
        String query = "SELECT * FROM Transactions WHERE transaction_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Simulate no result

        Transaction result = transactionDAO.getTransactionById(1);

        assertNull(result);
    }

    @Test
    void testGetAllTransactions_Success() throws SQLException {
        String query = "SELECT * FROM Transactions";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // One result, then end

        when(mockResultSet.getInt("transaction_id")).thenReturn(testTransaction.getTransactionId());
        when(mockResultSet.getInt("patron_id")).thenReturn(testTransaction.getPatronId());
        when(mockResultSet.getInt("book_id")).thenReturn(testTransaction.getBookId());
        when(mockResultSet.getDate("borrow_date")).thenReturn(Date.valueOf(testTransaction.getBorrowDate()));
        when(mockResultSet.getDate("due_date")).thenReturn(Date.valueOf(testTransaction.getDueDate()));
        when(mockResultSet.getBigDecimal("fine")).thenReturn(BigDecimal.ZERO);
        when(mockResultSet.getString("transaction_type")).thenReturn(testTransaction.getTransactionType().name());

        List<Transaction> transactions = transactionDAO.getAllTransactions();

        assertFalse(transactions.isEmpty(), "Transactions should be retrieved.");
        assertEquals(1, transactions.size(), "There should be one transaction in the result.");
    }

    @Test
    void testUpdateTransactionFine_Success() throws SQLException {
        String query = "UPDATE Transactions SET fine = ? WHERE transaction_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful update

        transactionDAO.updateTransactionFine(testTransaction.getTransactionId(), BigDecimal.valueOf(10.0));

        verify(mockPreparedStatement, times(1)).setBigDecimal(1, BigDecimal.valueOf(10.0));
        verify(mockPreparedStatement, times(1)).setInt(2, testTransaction.getTransactionId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteTransaction_Success() throws SQLException {
        String query = "DELETE FROM Transactions WHERE transaction_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful deletion

        transactionDAO.deleteTransaction(testTransaction.getTransactionId());

        verify(mockPreparedStatement, times(1)).setInt(1, testTransaction.getTransactionId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testBorrowBook_Success() throws SQLException {
        // Mock the behavior for checking book availability
        String checkAvailabilityQuery = "SELECT status FROM Books WHERE book_id = ?";
        when(mockConnection.prepareStatement(checkAvailabilityQuery)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("status")).thenReturn("available");

        // Mock the behavior for the insert transaction query
        String insertTransactionQuery = "INSERT INTO Transactions (patron_id, book_id, borrow_date, due_date, transaction_type) VALUES (?, ?, ?, ?, ?)";
        when(mockConnection.prepareStatement(insertTransactionQuery, Statement.RETURN_GENERATED_KEYS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful insert

        // Mock the generated keys ResultSet to return the transaction ID
        ResultSet mockGeneratedKeys = mock(ResultSet.class);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockGeneratedKeys.next()).thenReturn(true); // Simulate that we have a generated key
        when(mockGeneratedKeys.getInt(1)).thenReturn(123); // Simulate that the generated key is 123

        // Mock the behavior for the update query (pstmtUpdate)
        String updateQuery = "UPDATE Books SET status = ? WHERE book_id = ?";
        when(mockConnection.prepareStatement(updateQuery)).thenReturn(mockPreparedStatement); // Mock the update PreparedStatement
        doNothing().when(mockPreparedStatement).setString(anyInt(), anyString()); // Mock the setString call (doNothing() since we're not concerned with actual values here)
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful update

        // Call the method
        boolean result = transactionDAO.borrowBook(testTransaction.getPatronId(), testTransaction.getBookId());

        // Verify the result
        assertTrue(result);

        // Verify that executeUpdate() was called once for the insert and once for the update
        InOrder inOrder = inOrder(mockPreparedStatement);

        // First call: Insert
        inOrder.verify(mockPreparedStatement, times(1)).executeUpdate();

        // Second call: Update
        inOrder.verify(mockPreparedStatement, times(1)).executeUpdate();
    }


    @Test
    void testBorrowBook_BookNotAvailable() throws SQLException {
        String checkAvailabilityQuery = "SELECT status FROM Books WHERE book_id = ?";
        when(mockConnection.prepareStatement(checkAvailabilityQuery)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("status")).thenReturn("borrowed"); // Book not available

        boolean result = transactionDAO.borrowBook(testTransaction.getPatronId(), testTransaction.getBookId());

        assertFalse(result); // Should not be able to borrow the book
    }
}
