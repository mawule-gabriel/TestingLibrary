package Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class StaffTest {

    private Staff staff;

    @BeforeEach
    public void setUp() {
        // Create a new Staff instance before each test
        staff = new Staff(1, "Alice", "Johnson", "Librarian", "alice.johnson@example.com", "1234567890", LocalDate.of(2022, 5, 15), "password123");
    }

    @Test
    public void testConstructor() {
        // Test the constructor and check initial values
        assertEquals(1, staff.getStaffId());
        assertEquals("Alice", staff.getFirstName());
        assertEquals("Johnson", staff.getLastName());
        assertEquals("Librarian", staff.getRole());
        assertEquals("alice.johnson@example.com", staff.getEmail());
        assertEquals("1234567890", staff.getPhoneNumber());
        assertEquals(LocalDate.of(2022, 5, 15), staff.getHireDate());
        assertEquals("password123", staff.getPassword());
    }

    @Test
    public void testSettersAndGetters() {
        // Test setters and getters individually
        staff.setStaffId(2);
        staff.setFirstName("Bob");
        staff.setLastName("Smith");
        staff.setRole("Manager");
        staff.setEmail("bob.smith@example.com");
        staff.setPhoneNumber("0987654321");
        staff.setHireDate(LocalDate.of(2023, 6, 10));
        staff.setPassword("newpassword");

        assertEquals(2, staff.getStaffId());
        assertEquals("Bob", staff.getFirstName());
        assertEquals("Smith", staff.getLastName());
        assertEquals("Manager", staff.getRole());
        assertEquals("bob.smith@example.com", staff.getEmail());
        assertEquals("0987654321", staff.getPhoneNumber());
        assertEquals(LocalDate.of(2023, 6, 10), staff.getHireDate());
        assertEquals("newpassword", staff.getPassword());
    }

    @Test
    public void testToString() {
        // Test the toString method if implemented (you can override this method in the class if desired)
        String expected = "Staff{" +
                "staffId=" + 1 +
                ", firstName='Alice'" +
                ", lastName='Johnson'" +
                ", role='Librarian'" +
                ", email='alice.johnson@example.com'" +
                ", phoneNumber='1234567890'" +
                ", hireDate=" + LocalDate.of(2022, 5, 15) +
                ", password='password123'" +
                '}';
        assertEquals(expected, staff.toString());
    }
}
