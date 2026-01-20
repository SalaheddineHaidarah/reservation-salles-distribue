package ma.ensias.salles.rmi;

import ma.ensias.salles.model.Reservation;

import java.rmi.Naming;
import java.util.List;

public class RmiClient {

    public static void main(String[] args) {
        try {
            ReservationRMI service = (ReservationRMI) Naming.lookup(
                    "rmi://localhost:1099/ReservationService"
            );

            boolean ok1 = service.reserver(1, "salaheddine", "2026-01-20", "10:00", "11:00");
            boolean ok2 = service.reserver(1, "Mohammed",    "2026-01-20", "10:30", "11:30"); // conflict

            System.out.println("Reservation 1 (should be true): " + ok1);
            System.out.println("Reservation 2 (should be false): " + ok2);

            List<Reservation> mine = service.mesReservations("salaheddine");
            System.out.println("Mes reservations: " + mine.size());
            for (Reservation r : mine) {
                System.out.println(" - salle " + r.getSalleId() + " " + r.getDate()
                        + " " + r.getHeureDebut() + "-" + r.getHeureFin());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
