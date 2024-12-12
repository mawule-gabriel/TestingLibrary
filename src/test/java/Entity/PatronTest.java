package Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PatronTest {

    private Patron patron;

    @BeforeEach
    public void setUp() {
        // Create a new Patron instance before each test
        patron = new Patron(1, "John", "Doe", "john.doe@example.com", "1234567890", "123 Main St", LocalDate.of(2023, 1, 1));
    }

    @Test
    public void testConstructor() {
        // Test the constructor and check initial values
        assertEquals(1, patron.getPatronId());
        assertEquals("John", patron.getFirstName());
        assertEquals("Doe", patron.getLastName());
        assertEquals("john.doe@example.com", patron.getEmail());
        assertEquals("1234567890", patron.getPhoneNumber());
        assertEquals("123 Main St", patron.getAddress());
        assertEquals(LocalDate.of(2023, 1, 1), patron.getMembershipDate());
    }

    @Test
    public void testSettersAndGetters() {
        // Test setters and getters individually
        patron.setFirstName("Jane");
        patron.setLastName("Smith");
        patron.setEmail("jane.smith@example.com");
        patron.setPhoneNumber("0987654321");
        patron.setAddress("456 Another St");  // Updated the address

        assertEquals("Jane", patron.getFirstName());
        assertEquals("Smith", patron.getLastName());
        assertEquals("jane.smith@example.com", patron.getEmail());
        assertEquals("0987654321", patron.getPhoneNumber());
        assertEquals("456 Another St", patron.getAddress());  // Updated the expected address
    }

    @Test
    public void testToString() {
        // Test the toString method for correct output format
        String expected = "Patron{" +
                "patronId=" + 1 +
                ", firstName='John'" +
                ", lastName='Doe'" +
                ", email='john.doe@example.com'" +
                ", phoneNumber='1234567890'" +
                ", address='123 Main St'" +
                ", membershipDate=" + LocalDate.of(2023, 1, 1) +
                '}';
        assertEquals(expected, patron.toString());
    }
}
