package uce.edu.web.api.application.representation;

import java.util.List;

public class CursoRepresentation {
    public Integer id;
    
    public String nombre;
    public String codigo;
    public String descripcion;
    public Integer cupos;

    public String estado;
    public List<LinkDto> links;


}
