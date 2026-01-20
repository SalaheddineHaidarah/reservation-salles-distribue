package ma.ensias.salles.ws.rest;

import ma.ensias.salles.core.ReservationManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/salles")
@Produces(MediaType.APPLICATION_JSON)
public class SalleResource {

    private final ReservationManager manager = ReservationManager.getInstance();

    @GET
    @Path("/disponible")
    public boolean estDisponible(@QueryParam("id") int id,
                                 @QueryParam("date") String date,
                                 @QueryParam("debut") String debut,
                                 @QueryParam("fin") String fin) {
        return manager.estDisponible(id, date, debut, fin);
    }
}
