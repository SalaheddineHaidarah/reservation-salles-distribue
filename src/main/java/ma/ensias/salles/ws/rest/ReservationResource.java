package ma.ensias.salles.ws.rest;

import ma.ensias.salles.core.ReservationManager;
import ma.ensias.salles.model.Reservation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private final ReservationManager manager = ReservationManager.getInstance();

    @POST
    public Response reserver(Reservation r) {
        boolean ok = manager.reserver(
                r.getSalleId(),
                r.getUtilisateur(),
                r.getDate(),
                r.getHeureDebut(),
                r.getHeureFin()
        );

        if (!ok) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Salle not available")
                    .build();
        }

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/user/{username}")
    public List<Reservation> mesReservations(@PathParam("username") String user) {
        return manager.getReservationsByUser(user);
    }
}
