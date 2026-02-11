package uce.edu.web.api.interfaces;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import uce.edu.web.api.application.InscripcionService;
import uce.edu.web.api.application.representation.InscripcionRepresentation;
import uce.edu.web.api.application.representation.LinkDto;

@Path("/inscripciones")
public class InscripcionResource {

    @Inject
    private InscripcionService inscripcionService;

    @Context
    private UriInfo uriInfo;

    // Acción de matricular: Recibe IDs por QueryParam
    @POST
    @Path("/matricular")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response matricular(@QueryParam("estudianteId") Integer estudianteId, 
                               @QueryParam("cursoId") Integer cursoId) {
        this.inscripcionService.matricular(estudianteId, cursoId);
        // Retornamos 201 Created
        return Response.status(Response.Status.CREATED).build();
    }

    // Acción de cancelar: Borrado lógico mediante PUT (actualización de estado)
    @PUT
    @Path("/{id}/cancelar")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response cancelar(@PathParam("id") Integer id) {
        this.inscripcionService.cancelar(id);
        return Response.ok().build();
    }

    // Listar inscripciones filtradas por estudiante
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"admin", "user"})
    public List<InscripcionRepresentation> listarPorEstudiante(@QueryParam("estudianteId") Integer estudianteId) {
        List<InscripcionRepresentation> lista = new ArrayList<>();
        
        // Si no viene el ID, podríamos lanzar un error o devolver lista vacía
        if (estudianteId != null) {
            for (InscripcionRepresentation ir : this.inscripcionService.buscarPorEstudiante(estudianteId)) {
                lista.add(this.construirLinks(ir));
            }
        }
        
        return lista;
    }

    // Comentario arriba de la línea
    // Genera links inteligentes: Solo permite cancelar si la inscripción no está ya cancelada
    private InscripcionRepresentation construirLinks(InscripcionRepresentation ir) {
        List<LinkDto> links = new ArrayList<>();

        // Link Self: Para ver el detalle de esta inscripción
        String self = this.uriInfo.getBaseUriBuilder()
                .path(InscripcionResource.class)
                .path(String.valueOf(ir.id))
                .build().toString();
        links.add(new LinkDto(self, "self"));

        // HATEOAS CONDICIONAL: 
        // Si el estado es ACTIVA, enviamos el link para poder cancelarla.
        // Si ya está CANCELADA, el link no aparecerá, cumpliendo con el Nivel 3.
        if ("ACTIVA".equals(ir.estado)) {
            String cancelar = this.uriInfo.getBaseUriBuilder()
                    .path(InscripcionResource.class)
                    .path(String.valueOf(ir.id))
                    .path("cancelar")
                    .build().toString();
            links.add(new LinkDto(cancelar, "cancelar-inscripcion"));
        }

        ir.links = links;
        return ir;
    }
}