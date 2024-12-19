package Service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import DAO.BookDAO;
import Entity.Book;
import Entity.Enums.BookStatus;
import Exception.BookException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    private BookService bookService;
    private Book testBook;

    @BeforeEach
    void setUp() throws Exception {
        // Create actual BookService instance
        bookService = new BookService();

        // Create a sample book for testing
        testBook = new Book(
                1,
                "Test Book",
                "Test Author",
                2020,
                "Fiction",
                BookStatus.AVAILABLE,
                "1234567890"
        );
    }

    @Test
    void testAddBook_Success() throws BookException, SQLException {
        // Arrange
        BookDAO mockBookDAO = Mockito.mock(BookDAO.class);
        doNothing().when(mockBookDAO).addBook(any(Book.class));

        // Use reflection to set the mock BookDAO
        Whitebox.setInternalState(bookService, "bookDAO", mockBookDAO);

        // Act
        bookService.addBook(testBook);

        // Assert
        verify(mockBookDAO).addBook(testBook);
    }

    @Test
    void testAddBook_InvalidBook() {
        // Arrange
        Book invalidBook = new Book(
                2,
                "",  // Empty title
                "",  // Empty author
                500,  // Invalid publication year
                "Fiction",
                BookStatus.AVAILABLE,
                "invalid-isbn"
        );

        // Act & Assert
        BookException exception = assertThrows(BookException.class,
                () -> bookService.addBook(invalidBook));

        assertTrue(exception.getMessage().contains("Title cannot be empty"));
        assertTrue(exception.getMessage().contains("Author cannot be empty"));
        assertTrue(exception.getMessage().contains("Invalid publication year"));
        assertTrue(exception.getMessage().contains("Invalid ISBN format"));
    }




    @Test
    void testGetAllBooks_Success() throws BookException, SQLException {
        // Arrange
        BookDAO mockBookDAO = Mockito.mock(BookDAO.class);
        List<Book> bookList = new ArrayList<>();
        bookList.add(testBook);
        bookList.add(new Book(
                2,
                "Another Book",
                "Another Author",
                2021,
                "Non-Fiction",
                BookStatus.AVAILABLE,
                "0987654321"
        ));

        when(mockBookDAO.getAllBooks()).thenReturn(bookList);

        // Use reflection to set the mock BookDAO
        Whitebox.setInternalState(bookService, "bookDAO", mockBookDAO);

        // Act
        List<Book> retrievedBooks = bookService.getAllBooks();

        // Assert
        assertNotNull(retrievedBooks);
        assertEquals(2, retrievedBooks.size());
        verify(mockBookDAO).getAllBooks();
    }


}