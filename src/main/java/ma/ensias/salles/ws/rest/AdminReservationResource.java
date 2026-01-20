package ma.ensias.salles.ws.rest;

import ma.ensias.salles.dao.ReservationDAO;
import ma.ensias.salles.model.Reservation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/admin/reservations")
@Produces(MediaType.APPLICATION_JSON)
public class AdminReservationResource {

    private final ReservationDAO reservationDAO = new ReservationDAO();

    @GET
    public List<Reservation> getAll() {
        return reservationDAO.findAll();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        boolean ok = reservationDAO.deleteById(id);
        if (!ok) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}

