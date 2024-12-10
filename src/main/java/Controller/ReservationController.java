package Controller;

import Entity.Reservation;
import Entity.Enums.ReservationStatus;
import Service.ReservationService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController() {
        this.reservationService = new ReservationService();
    }

    // Add a new reservation
    public void addReservation(Reservation reservation) {
        try {
            reservationService.addReservation(reservation);
            System.out.println("Reservation added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding reservation: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
        }
    }



    // Get all reservations
    public List<Reservation> getAllReservations() {
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            if (reservations.isEmpty()) {
                System.out.println("No reservations found.");
            } else {
                reservations.forEach(System.out::println);  // Optional, for debugging
            }
            return reservations;  // Return the list of reservations
        } catch (SQLException e) {
            System.err.println("Error retrieving reservations: " + e.getMessage());
        }
        return new ArrayList<>();  // Return an empty list in case of an error
    }



    // Delete a reservation
    public void deleteReservation(int reservationId) {
        try {
            reservationService.deleteReservation(reservationId);
            System.out.println("Reservation deleted successfully.");
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
        }
    }
}
