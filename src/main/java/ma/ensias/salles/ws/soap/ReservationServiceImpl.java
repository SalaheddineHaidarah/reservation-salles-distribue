package ma.ensias.salles.ws.soap;

import ma.ensias.salles.core.ReservationManager;
import ma.ensias.salles.model.Reservation;

import javax.jws.WebService;
import java.util.List;

@WebService(endpointInterface = "ma.ensias.salles.ws.soap.ReservationService")
public class ReservationServiceImpl implements ReservationService {

    private final ReservationManager manager = ReservationManager.getInstance();

    @Override
    public boolean reserverSalle(int salleId, String user, String date, String heureDebut, String heureFin) {
        return manager.reserver(salleId, user, date, heureDebut, heureFin);
    }

    @Override
    public List<Reservation> mesReservations(String user) {
        return manager.getReservationsByUser(user);
    }
}
