package Controller;

import Entity.Book;
import Entity.Enums.BookStatus;
import Service.BookService;
import Exception.BookException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookControllerTest {

    @Mock
    private BookService bookService;

    private BookController bookController;

    private Book book;

    @BeforeEach
    public void setUp() {
        // Initialize the mock objects before each test
        MockitoAnnotations.openMocks(this);

        // Instantiate the controller and inject the mock BookService
        bookController = new BookController();
        bookController.setBookService(bookService);  // Inject the mock service

        // Initialize a test book
        book = new Book(1, "Test Book", "Author", 2024, "Fiction", BookStatus.AVAILABLE, "9781234567890");
    }

    @Test
    public void testAddBook_ValidBook() throws Exception {
        // Mock the behavior of the BookService's addBook method
        doNothing().when(bookService).addBook(any(Book.class));  // Simulating no action for addBook

        // Call the method under test
        bookController.addBook("Test Book", "Author", "Fiction", 2024, BookStatus.AVAILABLE);

        // Verify that the addBook method was invoked exactly once
        verify(bookService, times(1)).addBook(any(Book.class));
    }

    @Test
    public void testGetAllBooks() throws Exception {
        // Prepare mock data: a list of books with a single book
        ObservableList<Book> bookList = FXCollections.observableArrayList(book);
        when(bookService.getAllBooks()).thenReturn(bookList);

        // Call the method under test
        ObservableList<Book> result = bookController.getAllBooks();

        // Verify that the getAllBooks method was invoked once
        verify(bookService, times(1)).getAllBooks();

        // Assert that the result is not null and contains the expected book
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(book, result.get(0));
    }

    @Test
    public void testUpdateBookStatus() throws Exception {
        // Mock the behavior of the BookService to return a valid book
        when(bookService.getBookById(1)).thenReturn(book);
        doNothing().when(bookService).updateBookStatus(1, BookStatus.BORROWED);

        // Call the method under test to update the book status
        bookController.updateBookStatus(1, BookStatus.BORROWED);

        // Verify that the updateBookStatus method was called once
        verify(bookService, times(1)).updateBookStatus(1, BookStatus.BORROWED);
    }

    @Test
    public void testDeleteBook() throws Exception {
        // Simulate the scenario where the book cannot be deleted due to a foreign key constraint
        when(bookService.getBookById(1)).thenReturn(book);

        // Wrap the SQLException inside a RuntimeException
        doThrow(new RuntimeException("Foreign key constraint violation")).when(bookService).deleteBook(1);

        // Test the deleteBook method, which should throw a RuntimeException due to the constraint violation
        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookController.deleteBook(1);  // This should throw a RuntimeException
        });

        // Assert that the exception message contains the expected error
        assertTrue(exception.getMessage().contains("Foreign key constraint violation"));
    }

}
