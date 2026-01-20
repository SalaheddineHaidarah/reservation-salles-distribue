package ma.ensias.salles.ws.rest;

import ma.ensias.salles.dao.SalleDAO;
import ma.ensias.salles.model.Salle;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/admin/salles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminSalleResource {

    private final SalleDAO salleDAO = new SalleDAO();

    @GET
    public List<Salle> getAll() {
        return salleDAO.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") int id) {
        Salle s = salleDAO.findById(id);
        if (s == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(s).build();
    }

    @POST
    public Response create(Salle salle) {
        boolean ok = salleDAO.create(salle);
        if (!ok) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") int id, Salle salle) {
        salle.setId(id);
        boolean ok = salleDAO.update(salle);
        if (!ok) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        boolean ok = salleDAO.delete(id);
        if (!ok) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
