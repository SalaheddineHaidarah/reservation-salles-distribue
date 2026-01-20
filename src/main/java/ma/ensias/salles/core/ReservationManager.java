package ma.ensias.salles.core;

import ma.ensias.salles.model.Reservation;
import ma.ensias.salles.model.Salle;
import ma.ensias.salles.dao.SalleDAO;
import ma.ensias.salles.dao.ReservationDAO;

import java.util.List;

public class ReservationManager {

    private static final ReservationManager instance = new ReservationManager();

    private final SalleDAO salleDAO = new SalleDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    private ReservationManager() {
        // No in-memory reservations or idCounter
    }

    public static ReservationManager getInstance() {
        return instance;
    }

    public boolean estDisponible(int salleId, String date, String debut, String fin) {
        return reservationDAO.isAvailable(salleId, date, debut, fin);
    }

    public synchronized boolean reserver(int salleId, String user, String date, String debut, String fin) {
        if (!reservationDAO.isAvailable(salleId, date, debut, fin)) {
            return false;
        }
        return reservationDAO.createReservation(salleId, user, date, debut, fin);
    }

    public List<Reservation> getReservationsByUser(String user) {
        return reservationDAO.findByUser(user);
    }

    public List<Reservation> getReservations() {
        // Not implemented: add findAll() to ReservationDAO if needed
        throw new UnsupportedOperationException("Not implemented: getReservations() should use ReservationDAO");
    }

    public Salle getSalleById(int id) {
        for (Salle s : salleDAO.findAll()) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    public List<Salle> getSallesDisponibles(String date, String hDebut, String hFin) {
        List<Salle> disponibles = new java.util.ArrayList<>();
        for (Salle s : salleDAO.findAll()) {
            if (estDisponible(s.getId(), date, hDebut, hFin)) {
                disponibles.add(s);
            }
        }
        return disponibles;
    }

    public boolean annulerReservation(int reservationId) {
        // Not implemented: add delete/cancel to ReservationDAO if needed
        throw new UnsupportedOperationException("Not implemented: annulerReservation() should use ReservationDAO");
    }
}
