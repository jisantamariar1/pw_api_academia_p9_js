package uce.edu.web.api.interfaces;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import uce.edu.web.api.application.CursoService;
import uce.edu.web.api.application.representation.CursoRepresentation;
import uce.edu.web.api.application.representation.LinkDto;

@Path("/cursos")
public class CursoResource {

    @Inject
    private CursoService cursoService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "user", "docente"})
    public List<CursoRepresentation> listarTodos() {
        List<CursoRepresentation> lista = new ArrayList<>();
        for (CursoRepresentation cr : this.cursoService.listarTodos()) {
            lista.add(this.construirLinks(cr));
        }
        return lista;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "user"})
    public CursoRepresentation consultarPorId(@PathParam("id") Integer id) {
        CursoRepresentation cr = this.cursoService.consultarPorId(id);
        if (cr != null) {
            return this.construirLinks(cr);
        }
        return null;
    }

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response guardar(CursoRepresentation curso) {
        this.cursoService.crear(curso);
        return Response.status(Response.Status.CREATED).entity(this.construirLinks(curso)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response actualizar(@PathParam("id") Integer id, CursoRepresentation curso) {
        this.cursoService.actualizar(curso, id);
        return Response.status(Response.Status.OK).entity(null).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public void actualizarParcial(@PathParam("id") Integer id, CursoRepresentation curso) {
        this.cursoService.actualizarParcial(curso, id);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public void borrar(@PathParam("id") Integer id) {
        this.cursoService.borrar(id);
    }


    // Construye los enlaces HATEOAS para el recurso Curso
    private CursoRepresentation construirLinks(CursoRepresentation cr) {
        if (cr.id == null) return cr;
        // Enlace al propio curso
        String self = this.uriInfo.getBaseUriBuilder()
                .path(CursoResource.class)
                .path(String.valueOf(cr.id))
                .build().toString();

        // Enlace para matricularse en este curso (apunta al endpoint de inscripciones)
        String matricular = this.uriInfo.getBaseUriBuilder()
                .path(InscripcionResource.class)
                .path("matricular")
                .queryParam("cursoId", cr.id)
                .build().toString();

        // HATEOAS: El link de matricular solo debería ser útil si hay cupos
        List<LinkDto> links = new ArrayList<>();
        links.add(new LinkDto(self, "self"));
        
        if (cr.cupos > 0 && "ABIERTO".equals(cr.estado)) {
            links.add(new LinkDto(matricular, "matricular-estudiante"));
        }

        cr.links = links;
        return cr;
    }
}