package Service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import DAO.StaffDAO;
import Entity.Staff;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class StaffServiceTest {

    @Mock
    private StaffDAO staffDAO;

    private StaffService staffService;
    private Staff testStaff;

    @BeforeEach
    void setUp() throws Exception {
        // Create actual StaffService instance
        staffService = new StaffService();

        // Use reflection to set the staffDAO field
        Field staffDAOField = StaffService.class.getDeclaredField("staffDAO");
        staffDAOField.setAccessible(true);
        staffDAOField.set(staffService, staffDAO);

        // Create a valid staff member for testing
        testStaff = new Staff(
                1,
                "John",
                "Doe",
                "Librarian",
                "john.doe@library.com",
                "1234567890",
                LocalDate.now().minusDays(30),
                "password123"
        );
    }

    @Test
    void testAddStaff_Success() {
        // Arrange
        doNothing().when(staffDAO).addStaff(any(Staff.class));

        // Act
        staffService.addStaff(testStaff);

        // Assert
        verify(staffDAO).addStaff(testStaff);
    }

    @Test
    void testAddStaff_NullFirstName() {
        // Arrange
        Staff invalidStaff = new Staff(
                1,
                "",
                "Doe",
                "Librarian",
                "john.doe@library.com",
                "1234567890",
                LocalDate.now().minusDays(30),
                "password123"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> staffService.addStaff(invalidStaff));

        assertTrue(exception.getMessage().contains("First name cannot be null or empty"));
    }

    @Test
    void testAddStaff_NullLastName() {
        // Arrange
        Staff invalidStaff = new Staff(
                1,
                "John",
                "",
                "Librarian",
                "john.doe@library.com",
                "1234567890",
                LocalDate.now().minusDays(30),
                "password123"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> staffService.addStaff(invalidStaff));

        assertTrue(exception.getMessage().contains("Last name cannot be null or empty"));
    }

    @Test
    void testAddStaff_NullRole() {
        // Arrange
        Staff invalidStaff = new Staff(
                1,
                "John",
                "Doe",
                "",
                "john.doe@library.com",
                "1234567890",
                LocalDate.now().minusDays(30),
                "password123"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> staffService.addStaff(invalidStaff));

        assertTrue(exception.getMessage().contains("Role cannot be null or empty"));
    }

    @Test
    void testAddStaff_InvalidEmail() {
        // Arrange
        Staff invalidStaff = new Staff(
                1,
                "John",
                "Doe",
                "Librarian",
                "invalid-email",
                "1234567890",
                LocalDate.now().minusDays(30),
                "password123"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> staffService.addStaff(invalidStaff));

        assertTrue(exception.getMessage().contains("Invalid email address"));
    }

    @Test
    void testAddStaff_InvalidPhoneNumber() {
        // Arrange
        Staff invalidStaff = new Staff(
                1,
                "John",
                "Doe",
                "Librarian",
                "john.doe@library.com",
                "123",
                LocalDate.now().minusDays(30),
                "password123"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> staffService.addStaff(invalidStaff));

        assertTrue(exception.getMessage().contains("Invalid phone number"));
    }

    @Test
    void testAddStaff_FutureHireDate() {
        // Arrange
        Staff invalidStaff = new Staff(
                1,
                "John",
                "Doe",
                "Librarian",
                "john.doe@library.com",
                "1234567890",
                LocalDate.now().plusDays(30),
                "password123"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> staffService.addStaff(invalidStaff));

        assertTrue(exception.getMessage().contains("Hire date cannot be in the future"));
    }

    @Test
    void testGetAllStaff_Success() {
        // Arrange
        List<Staff> staffList = new ArrayList<>();
        staffList.add(testStaff);
        staffList.add(new Staff(
                2,
                "Jane",
                "Smith",
                "Manager",
                "jane.smith@library.com",
                "0987654321",
                LocalDate.now().minusDays(60),
                "password456"
        ));

        when(staffDAO.getAllStaff()).thenReturn(staffList);

        // Act
        List<Staff> resultStaff = staffService.getAllStaff();

        // Assert
        assertNotNull(resultStaff);
        assertEquals(2, resultStaff.size());
        verify(staffDAO).getAllStaff();
    }
}