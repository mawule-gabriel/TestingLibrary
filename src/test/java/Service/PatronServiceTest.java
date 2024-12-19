package Service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import DAO.PatronDAO;
import Entity.Patron;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PatronServiceTest {

    @Mock
    private PatronDAO patronDAO;

    private PatronService patronService;
    private Patron testPatron;

    @BeforeEach
    void setUp() throws Exception {
        // Create actual PatronService instance
        patronService = new PatronService();

        // Use reflection to set the patronDAO field
        Field patronDAOField = PatronService.class.getDeclaredField("patronDAO");
        patronDAOField.setAccessible(true);
        patronDAOField.set(patronService, patronDAO);

        // Create a valid patron for testing
        testPatron = new Patron(
                1,
                "John",
                "Doe",
                "john.doe@example.com",
                "1234567890",
                "123 Test Street",
                LocalDate.now()
        );
    }

    @Test
    void testAddPatron_Success() throws SQLException {
        // Arrange
        doNothing().when(patronDAO).addPatron(any(Patron.class));

        // Act
        patronService.addPatron(testPatron);

        // Assert
        verify(patronDAO).addPatron(testPatron);
    }

    @Test
    void testAddPatron_NullFirstName() {
        // Arrange
        Patron invalidPatron = new Patron(
                1,
                "",
                "Doe",
                "john.doe@example.com",
                "1234567890",
                "123 Test Street",
                LocalDate.now()
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patronService.addPatron(invalidPatron));

        assertTrue(exception.getMessage().contains("Patron first name cannot be null or empty"));
    }

    @Test
    void testAddPatron_NullLastName() {
        // Arrange
        Patron invalidPatron = new Patron(
                1,
                "John",
                "",
                "john.doe@example.com",
                "1234567890",
                "123 Test Street",
                LocalDate.now()
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patronService.addPatron(invalidPatron));

        assertTrue(exception.getMessage().contains("Patron last name cannot be null or empty"));
    }

    @Test
    void testAddPatron_NullEmail() {
        // Arrange
        Patron invalidPatron = new Patron(
                1,
                "John",
                "Doe",
                "",
                "1234567890",
                "123 Test Street",
                LocalDate.now()
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patronService.addPatron(invalidPatron));

        assertTrue(exception.getMessage().contains("Patron email cannot be null or empty"));
    }


    @Test
    void testGetAllPatrons_Success() throws SQLException {
        // Arrange
        List<Patron> patronList = new ArrayList<>();
        patronList.add(testPatron);
        patronList.add(new Patron(
                2,
                "Jane",
                "Smith",
                "jane.smith@example.com",
                "0987654321",
                "456 Test Avenue",
                LocalDate.now()
        ));

        when(patronDAO.getAllPatrons()).thenReturn(patronList);

        // Act
        List<Patron> resultPatrons = patronService.getAllPatrons();

        // Assert
        assertNotNull(resultPatrons);
        assertEquals(2, resultPatrons.size());
        verify(patronDAO).getAllPatrons();
    }



    @Test
    void testUpdatePatronAddress_EmptyAddress() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patronService.updatePatronAddress(1, ""));

        assertTrue(exception.getMessage().contains("Address cannot be null or empty"));
    }


    @Test
    void testSearchPatronsByName_Success() throws SQLException {
        // Arrange
        String searchName = "John";
        List<Patron> patronList = new ArrayList<>();
        patronList.add(testPatron);

        when(patronDAO.searchPatronsByName(searchName)).thenReturn(patronList);

        // Act
        List<Patron> resultPatrons = patronService.searchPatronsByName(searchName);

        // Assert
        assertNotNull(resultPatrons);
        assertFalse(resultPatrons.isEmpty());
        assertEquals(1, resultPatrons.size());
        verify(patronDAO).searchPatronsByName(searchName);
    }
}