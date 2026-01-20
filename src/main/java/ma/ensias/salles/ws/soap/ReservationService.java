package ma.ensias.salles.ws.soap;

import ma.ensias.salles.model.Reservation;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface ReservationService {

    @WebMethod
    boolean reserverSalle(
            @WebParam(name = "salleId") int salleId,
            @WebParam(name = "user") String user,
            @WebParam(name = "date") String date,
            @WebParam(name = "heureDebut") String heureDebut,
            @WebParam(name = "heureFin") String heureFin
    );

    @WebMethod
    List<Reservation> mesReservations(
            @WebParam(name = "user") String user
    );
}
