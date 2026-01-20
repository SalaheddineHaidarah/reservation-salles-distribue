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

    @GET
    public List<Reservation> getAllReservations() {
        return manager.getReservations();
    }

    @GET
    @Path("/user/{user}")
    public List<Reservation> getReservationsByUser(@PathParam("user") String user) {
        return manager.getReservationsByUser(user);
    }

    @POST
    public Response createReservation(
            @QueryParam("salleId") int salleId,
            @QueryParam("user") String user,
            @QueryParam("date") String date,
            @QueryParam("heureDebut") String heureDebut,
            @QueryParam("heureFin") String heureFin) {

        boolean success = manager.reserver(salleId, user, date, heureDebut, heureFin);

        if (success) {
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\":\"Reservation created successfully\"}")
                    .build();
        }
        return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\":\"Reservation conflict or invalid data\"}")
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response cancelReservation(@PathParam("id") int id) {
        boolean success = manager.annulerReservation(id);

        if (success) {
            return Response.ok("{\"message\":\"Reservation cancelled\"}").build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"Reservation not found\"}")
                .build();
    }
}

