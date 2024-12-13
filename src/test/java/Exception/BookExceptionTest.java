package Exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookExceptionTest {

    // Test that the exception is created with a message and that message is returned correctly
    @Test
    public void testBookExceptionWithMessage() {
        // Arrange
        String errorMessage = "Book not found";

        // Act
        BookException exception = new BookException(errorMessage);

        // Assert
        assertNotNull(exception); // Ensure exception is not null
        assertEquals(errorMessage, exception.getMessage()); // Ensure the message is as expected
    }

    // Test that the exception is created with a message and a cause and both are returned correctly
    @Test
    public void testBookExceptionWithMessageAndCause() {
        // Arrange
        String errorMessage = "Book not found";
        Throwable cause = new RuntimeException("Database error");

        // Act
        BookException exception = new BookException(errorMessage, cause);

        // Assert
        assertNotNull(exception); // Ensure exception is not null
        assertEquals(errorMessage, exception.getMessage()); // Ensure the message is as expected
        assertEquals(cause, exception.getCause()); // Ensure the cause is set correctly
    }
}
