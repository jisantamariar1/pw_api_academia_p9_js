package uce.edu.web.api.application.representation;

import java.time.LocalDate;
import java.util.List;

public class InscripcionRepresentation {
    public Integer id;

    // En lugar de objetos complejos, aplanamos la info para mostrar lo importante
    // Esto facilita mucho la lectura del JSON en el frontend
    public String nombreEstudiante;
    public String nombreCurso;

    public LocalDate fecha;
    public String estado; // "ACTIVA", "CANCELADA"

    // AQUÍ ES CRUCIAL HATEOAS
    // Un link podría ser: "self" (ver detalle)
    // OTRO link clave: "cancelar" (PUT para cambiar estado)
    public List<LinkDto> links;

}
