package uce.edu.web.api.interfaces;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import uce.edu.web.api.application.EstudianteService;
import uce.edu.web.api.application.representation.EstudianteRepresentation;
import uce.edu.web.api.application.representation.LinkDto;

@Path("/estudiantes")
public class EstudianteResource {

    @Inject
    private EstudianteService estudianteService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "user", "docente"})
    public List<EstudianteRepresentation> listarTodos() {
        List<EstudianteRepresentation> lista = new ArrayList<>();
        for (EstudianteRepresentation er : this.estudianteService.listarTodos()) {
            lista.add(this.construirLinks(er));
        }
        return lista;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public EstudianteRepresentation consultarPorId(@PathParam("id") Integer id) {
        EstudianteRepresentation er = this.estudianteService.consultarPorId(id);
        if (er != null) {
            return this.construirLinks(er);
        }
        return null;
    }

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response guardar(EstudianteRepresentation estu) {
        this.estudianteService.crear(estu);
        return Response.status(Response.Status.CREATED).entity(this.construirLinks(estu)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response actualizar(@PathParam("id") Integer id, EstudianteRepresentation est) {
        this.estudianteService.actualizar(est, id);
        
        return Response.status(Response.Status.OK).entity(null).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public void actualizarParcial(@PathParam("id") Integer id, EstudianteRepresentation est) {
        this.estudianteService.actualizarParcial(est, id);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public void borrar(@PathParam("id") Integer id) {
        this.estudianteService.borrar(id);
    }

    
    // Construye los enlaces HATEOAS para el recurso Estudiante
    private EstudianteRepresentation construirLinks(EstudianteRepresentation er) {
        if (er.id == null) return er;
        // Enlace al propio recurso (self)
        String self = this.uriInfo.getBaseUriBuilder()
                .path(EstudianteResource.class)
                .path(String.valueOf(er.id))
                .build().toString();

        // Enlace a las inscripciones de este estudiante puntual (buscando en InscripcionResource)
        // Usamos queryParam para que el backend de inscripciones filtre por este ID
        String inscripciones = this.uriInfo.getBaseUriBuilder()
                .path(InscripcionResource.class)
                .queryParam("estudianteId", er.id)
                .build().toString();

        er.links = List.of(
                new LinkDto(self, "self"),
                new LinkDto(inscripciones, "mis-inscripciones")
        );
        return er;
    }
}