package ma.ensias.salles.core;

import ma.ensias.salles.model.Reservation;
import ma.ensias.salles.model.Salle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ReservationManager {

    private static final ReservationManager instance = new ReservationManager();

    private final List<Salle> salles = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private final AtomicInteger reservationId = new AtomicInteger(1);

    private ReservationManager() {
        // Données initiales (selon l’énoncé: petite/moyenne/grande)
        salles.add(new Salle(1, "petite", 6, "tableau blanc, projecteur"));
        salles.add(new Salle(2, "moyenne", 12, "tableau blanc, projecteur, videoconference"));
        salles.add(new Salle(3, "grande", 25, "tableau blanc, projecteur, videoconference, microphones, ecran"));
    }

    public static ReservationManager getInstance() {
        return instance;
    }

    public List<Salle> getSalles() {
        return salles;
    }

    public synchronized boolean estDisponible(int salleId, String date, String hDebut, String hFin) {
        for (Reservation r : reservations) {
            if (r.getSalleId() == salleId && r.getDate().equals(date)) {
                // chevauchement si NOT( fin<=debutExistant OR debut>=finExistant )
                boolean pasDeChevauchement =
                        (hFin.compareTo(r.getHeureDebut()) <= 0) ||
                                (hDebut.compareTo(r.getHeureFin()) >= 0);

                if (!pasDeChevauchement) {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized boolean reserver(int salleId, String user, String date, String hDebut, String hFin) {
        if (!estDisponible(salleId, date, hDebut, hFin)) {
            return false;
        }
        int id = reservationId.getAndIncrement();
        reservations.add(new Reservation(id, user, salleId, date, hDebut, hFin));
        return true;
    }

    public synchronized List<Reservation> getReservationsByUser(String user) {
        List<Reservation> res = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getUtilisateur().equals(user)) {
                res.add(r);
            }
        }
        return res;
    }

    public synchronized List<Reservation> getReservations() {
        return new ArrayList<>(reservations);
    }

    public Salle getSalleById(int id) {
        for (Salle s : salles) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    public synchronized List<Salle> getSallesDisponibles(String date, String hDebut, String hFin) {
        List<Salle> disponibles = new ArrayList<>();
        for (Salle s : salles) {
            if (estDisponible(s.getId(), date, hDebut, hFin)) {
                disponibles.add(s);
            }
        }
        return disponibles;
    }

    public synchronized boolean annulerReservation(int reservationId) {
        return reservations.removeIf(r -> r.getId() == reservationId);
    }
}
