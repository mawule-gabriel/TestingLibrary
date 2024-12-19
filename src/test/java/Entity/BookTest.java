package Entity;

import Entity.Enums.BookStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    private Book book;

    @BeforeEach
    public void setUp() {
        // Create a new Book instance before each test
        book = new Book(1, "Test Book", "Test Author", 2024, "Fiction", BookStatus.AVAILABLE, "9781234567890");
    }

    @Test
    public void testConstructor() {
        // Test the constructor and check initial values
        assertEquals(1, book.getBookId());
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals(2024, book.getPublicationYear());
        assertEquals("Fiction", book.getGenre());
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
        assertEquals("9781234567890", book.getIsbn());
    }

    @Test
    public void testSettersAndGetters() {
        // Test setters and getters individually
        book.setBookId(2);
        book.setTitle("Updated Book");
        book.setAuthor("Updated Author");
        book.setPublicationYear(2025);
        book.setGenre("Science Fiction");
        book.setStatus(String.valueOf(BookStatus.BORROWED));
        book.setIsbn("9780987654321");

        assertEquals(2, book.getBookId());
        assertEquals("Updated Book", book.getTitle());
        assertEquals("Updated Author", book.getAuthor());
        assertEquals(2025, book.getPublicationYear());
        assertEquals("Science Fiction", book.getGenre());
        assertEquals(BookStatus.BORROWED, book.getStatus());
        assertEquals("9780987654321", book.getIsbn());
    }

    @Test
    public void testSetStatusWithString() {
        // Test the setter with a string input
        book.setStatus("RESERVED");
        assertEquals(BookStatus.RESERVED, book.getStatus());
    }

    @Test
    public void testToString() {
        // Test the toString method for correct output format
        String expected = "Book{" +
                "bookId=" + 1 +
                ", title='Test Book'" +
                ", author='Test Author'" +
                ", publicationYear=" + 2024 +
                ", genre='Fiction'" +
                ", status='AVAILABLE'" +
                ", isbn='9781234567890'" +
                '}';
        assertEquals(expected, book.toString());
    }
}
