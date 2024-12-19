package Controller;

import Entity.Patron;
import Service.PatronService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PatronControllerTest {

    @Mock
    private PatronService patronService;

    private PatronController patronController;
    private Patron patron;

    @BeforeEach
    public void setUp() {
        // Initialize mock objects
        MockitoAnnotations.openMocks(this);

        // Instantiate PatronController and inject the mock PatronService
        patronController = new PatronController();
        patronController.setPatronService(patronService); // Inject the mock service

        // Initialize a test Patron with 7 parameters
        patron = new Patron(1, "John", "Doe", "john.doe@example.com", "555-1234", "123 Main St", LocalDate.now());
    }


    @Test
    public void testGetPatronById_Found() throws SQLException {
        // Mock the PatronService to return a valid Patron
        when(patronService.getPatronById(1)).thenReturn(patron);

        // Call the method under test
        Patron result = patronController.getPatronById(1);

        // Verify the interaction with PatronService
        verify(patronService, times(1)).getPatronById(1);

        // Assert the result
        assertNotNull(result);
        assertEquals(patron.getFirstName(), result.getFirstName());
    }

    @Test
    public void testGetPatronById_NotFound() throws SQLException {
        // Mock the PatronService to return null
        when(patronService.getPatronById(1)).thenReturn(null);

        // Call the method under test
        Patron result = patronController.getPatronById(1);

        // Verify the interaction with PatronService
        verify(patronService, times(1)).getPatronById(1);

        // Assert that the result is null
        assertNull(result);
    }

    @Test
    public void testGetAllPatrons() throws SQLException {
        // Mock the PatronService to return a list containing one Patron
        when(patronService.getAllPatrons()).thenReturn(List.of(patron));

        // Call the method under test
        List<Patron> result = patronController.getAllPatrons();

        // Verify the interaction with PatronService
        verify(patronService, times(1)).getAllPatrons();

        // Assert that the result is not null and contains the expected Patron
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(patron.getFirstName(), result.get(0).getFirstName());
    }

    @Test
    public void testUpdatePatronAddress() throws SQLException {
        // Mock the PatronService to perform the address update
        doNothing().when(patronService).updatePatronAddress(1, "456 New Address");

        // Call the method under test
        patronController.updatePatronAddress(1, "456 New Address");

        // Verify the interaction with PatronService
        verify(patronService, times(1)).updatePatronAddress(1, "456 New Address");
    }

    @Test
    public void testDeletePatron_Success() throws SQLException {
        // Mock the PatronService to delete a Patron
        doNothing().when(patronService).deletePatron(1);

        // Call the method under test
        patronController.deletePatron(1);

        // Verify the interaction with PatronService
        verify(patronService, times(1)).deletePatron(1);
    }

    @Test
    public void testDeletePatron_NotFound() throws SQLException {
        // Mock the PatronService to throw an exception when deleting a non-existent patron
        doThrow(new SQLException("Patron not found")).when(patronService).deletePatron(1);

        // Test that the exception is handled correctly
        Exception exception = assertThrows(SQLException.class, () -> {
            patronController.deletePatron(1);
        });

        // Assert the exception message
        assertTrue(exception.getMessage().contains("Patron not found"));
    }

}
