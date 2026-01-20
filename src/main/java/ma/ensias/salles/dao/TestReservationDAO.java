package ma.ensias.salles.dao;

import ma.ensias.salles.model.Reservation;

import java.util.List;

public class TestReservationDAO {

    public static void main(String[] args) {
        ReservationDAO dao = new ReservationDAO();

        boolean ok1 = dao.isAvailable(1, "2026-01-20", "10:00", "11:00");
        System.out.println("Available before insert (expect true): " + ok1);

        boolean created = dao.createReservation(1, "salaheddine", "2026-01-20", "10:00", "11:00");
        System.out.println("Created (expect true): " + created);

        boolean ok2 = dao.isAvailable(1, "2026-01-20", "10:30", "11:30");
        System.out.println("Available after conflict (expect false): " + ok2);

        List<Reservation> mine = dao.findByUser("salaheddine");
        System.out.println("My reservations: " + mine.size());
        for (Reservation r : mine) {
            System.out.println(r.getId() + " | salle " + r.getSalleId() + " | " +
                    r.getDate() + " " + r.getHeureDebut() + "-" + r.getHeureFin());
        }
    }
}

