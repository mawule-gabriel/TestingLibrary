package Controller;

import Controller.StaffController;
import Entity.Staff;
import Service.StaffService;
import DAO.StaffDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class StaffControllerTest {

    private StaffController staffController;
    private StaffService staffService;
    private StaffDAO staffDAO;
    private Staff staff;

    @BeforeEach
    public void setUp() {
        // Mock the dependencies
        staffDAO = mock(StaffDAO.class);
        staffService = mock(StaffService.class);

        // Inject mocked dependencies into StaffController
        staffController = new StaffController(staffService, staffDAO);

        // Create a sample staff object
        staff = new Staff(1, "John", "Doe", "Librarian", "johndoe@example.com", "1234567890", LocalDate.now(), "password123");
    }

    // Test for adding a staff member
    @Test
    public void testAddStaff_Success() {
        // Arrange: No exception should be thrown when valid data is passed
        doNothing().when(staffService).addStaff(any(Staff.class));

        // Act: Call addStaff method on controller
        staffController.addStaff(staff);

        // Assert: Verify that the addStaff method of service was called once with the staff object
        verify(staffService, times(1)).addStaff(staff);
    }


    // Test for retrieving all staff members
    @Test
    public void testGetAllStaff_Success() {
        // Arrange: Prepare a list of staff to be returned by the mock service
        List<Staff> staffList = Arrays.asList(staff);
        when(staffService.getAllStaff()).thenReturn(staffList);

        // Act: Call getAllStaff method on controller
        staffController.getAllStaff();

        // Assert: Verify that getAllStaff method was called once on service
        verify(staffService, times(1)).getAllStaff();
    }

    @Test
    public void testGetAllStaff_NoStaffFound() {
        // Arrange: Simulate an empty staff list being returned
        when(staffService.getAllStaff()).thenReturn(Arrays.asList());

        // Act: Call getAllStaff method on controller
        staffController.getAllStaff();

        // Assert: Verify that getAllStaff method was called once on service
        verify(staffService, times(1)).getAllStaff();
    }

    // Test for staff authentication
    @Test
    public void testAuthenticateStaff_Success() {
        // Arrange: Mock the staffDAO to return a staff object when valid credentials are provided
        when(staffDAO.getStaffByCredentials("johndoe@example.com", "password123")).thenReturn(staff);

        // Act: Call authenticateStaff method on controller
        Staff authenticatedStaff = staffController.authenticateStaff("johndoe@example.com", "password123");

        // Assert: Verify that the returned staff is the same as the mock staff
        assertNotNull(authenticatedStaff);
        assertEquals("John", authenticatedStaff.getFirstName());
        assertEquals("Doe", authenticatedStaff.getLastName());
    }

    @Test
    public void testAuthenticateStaff_Failure_InvalidCredentials() {
        // Arrange: Mock the staffDAO to return null for invalid credentials
        when(staffDAO.getStaffByCredentials("wrongemail@example.com", "wrongpassword")).thenReturn(null);

        // Act: Call authenticateStaff method on controller with invalid credentials
        Staff authenticatedStaff = staffController.authenticateStaff("wrongemail@example.com", "wrongpassword");

        // Assert: Verify that no staff is returned
        assertNull(authenticatedStaff);
    }
}
