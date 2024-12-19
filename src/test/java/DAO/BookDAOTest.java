package DAO;

import DatabaseConnection.DatabaseUtil;
import Entity.Book;
import Entity.Enums.BookStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.*;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BookDAOTest {

    @InjectMocks
    private BookDAO bookDAO;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private Book testBook;

    @BeforeEach
    void setUp() throws SQLException {
        // Reset all mocks to ensure a clean state for each test
        Mockito.clearAllCaches();
        MockitoAnnotations.openMocks(this);

        testBook = new Book(1, "Java Programming", "John Doe", 2020, "Programming", BookStatus.AVAILABLE, "1234567890");

        // Mock the static method for getting a connection (only once)
        mockStatic(DatabaseUtil.class);
        when(DatabaseUtil.getConnection()).thenReturn(mockConnection);

        // Reset mocks to ensure no leftover behavior
        reset(mockConnection, mockPreparedStatement, mockResultSet);
    }

    @Test
    void testGetBookById_Success() throws SQLException {
        String query = "SELECT * FROM Books WHERE book_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("book_id")).thenReturn(1);
        when(mockResultSet.getString("title")).thenReturn("Java Programming");
        when(mockResultSet.getString("author")).thenReturn("John Doe");
        when(mockResultSet.getInt("publication_year")).thenReturn(2020);
        when(mockResultSet.getString("genre")).thenReturn("Programming");
        when(mockResultSet.getString("status")).thenReturn("available");
        when(mockResultSet.getString("isbn")).thenReturn("1234567890");

        Book result = bookDAO.getBookById(1);

        assertNotNull(result);
        assertEquals("Java Programming", result.getTitle());
        assertEquals(1, result.getBookId());
        assertEquals("John Doe", result.getAuthor());
    }

    @Test
    void testGetBookById_NotFound() throws SQLException {
        String query = "SELECT * FROM Books WHERE book_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Simulate no result found

        Book result = bookDAO.getBookById(testBook.getBookId());

        assertNull(result, "Book should not be found with the given ID.");
    }

    @Test
    void testSearchBooks_Success() throws SQLException {
        String query = "SELECT * FROM Books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate that there is data in the ResultSet
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // One result, then end

        when(mockResultSet.getInt("book_id")).thenReturn(testBook.getBookId());
        when(mockResultSet.getString("title")).thenReturn("Java Programming");
        when(mockResultSet.getString("author")).thenReturn(testBook.getAuthor());
        when(mockResultSet.getInt("publication_year")).thenReturn(testBook.getPublicationYear());
        when(mockResultSet.getString("genre")).thenReturn(testBook.getGenre());
        when(mockResultSet.getString("status")).thenReturn(testBook.getStatus().toString().toLowerCase());
        when(mockResultSet.getString("isbn")).thenReturn(testBook.getIsbn());

        var books = bookDAO.searchBooks("Java");

        assertFalse(books.isEmpty(), "Books should be found when searched with the keyword.");
        assertEquals("Java Programming", books.get(0).getTitle(), "The book title should match the search result.");
    }

    @Test
    void testDeleteBook_Success() throws SQLException {
        String query = "DELETE FROM Books WHERE book_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        bookDAO.deleteBook(testBook.getBookId());

        verify(mockPreparedStatement, times(1)).setInt(1, testBook.getBookId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteBook_ForeignKeyConstraintViolation() throws SQLException {
        String query = "DELETE FROM Books WHERE book_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        doThrow(new SQLIntegrityConstraintViolationException("Foreign key constraint fails"))
                .when(mockPreparedStatement).executeUpdate();

        assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            bookDAO.deleteBook(testBook.getBookId());
        });
    }

    @Test
    void testAddBook_Success() throws SQLException {
        String query = "INSERT INTO Books (title, author, publication_year, genre, status, isbn) VALUES (?, ?, ?, ?, ?, ?)";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);

        bookDAO.addBook(testBook);

        verify(mockPreparedStatement, times(1)).setString(1, testBook.getTitle());
        verify(mockPreparedStatement, times(1)).setString(2, testBook.getAuthor());
        verify(mockPreparedStatement, times(1)).setInt(3, testBook.getPublicationYear());
        verify(mockPreparedStatement, times(1)).setString(4, testBook.getGenre());
        verify(mockPreparedStatement, times(1)).setString(5, testBook.getStatus().toString().toLowerCase());
        verify(mockPreparedStatement, times(1)).setString(6, testBook.getIsbn());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetAllBooks_Success() throws SQLException {
        String query = "SELECT * FROM Books";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // Simulate one result

        when(mockResultSet.getInt("book_id")).thenReturn(testBook.getBookId());
        when(mockResultSet.getString("title")).thenReturn(testBook.getTitle());
        when(mockResultSet.getString("author")).thenReturn(testBook.getAuthor());
        when(mockResultSet.getInt("publication_year")).thenReturn(testBook.getPublicationYear());
        when(mockResultSet.getString("genre")).thenReturn(testBook.getGenre());
        when(mockResultSet.getString("status")).thenReturn(testBook.getStatus().toString().toLowerCase());
        when(mockResultSet.getString("isbn")).thenReturn(testBook.getIsbn());

        List<Book> books = bookDAO.getAllBooks();

        assertFalse(books.isEmpty(), "Books should be retrieved.");
        assertEquals(1, books.size(), "There should be one book in the result.");
        assertEquals(testBook.getTitle(), books.get(0).getTitle(), "The title of the book should match.");
    }

    @Test
    void testUpdateBookStatus_Success() throws SQLException {
        String query = "UPDATE Books SET status = ? WHERE book_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful update

        bookDAO.updateBookStatus(testBook.getBookId(), BookStatus.BORROWED);

        verify(mockPreparedStatement, times(1)).setString(1, BookStatus.BORROWED.name().toLowerCase());
        verify(mockPreparedStatement, times(1)).setInt(2, testBook.getBookId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateBookStatus_Exception() throws SQLException {
        String query = "UPDATE Books SET status = ? WHERE book_id = ?";
        when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
        doThrow(new SQLException("Database error")).when(mockPreparedStatement).executeUpdate();

        assertThrows(SQLException.class, () -> {
            bookDAO.updateBookStatus(testBook.getBookId(), BookStatus.BORROWED);
        });
    }

    @Test
    void testGetBookById_Exception() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class, () -> {
            bookDAO.getBookById(testBook.getBookId());
        });
    }
}
