package uce.edu.web.api.application;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import uce.edu.web.api.application.representation.EstudianteRepresentation;
import uce.edu.web.api.domain.Estudiante;
import uce.edu.web.api.infraestructure.EstudianteRepository;

@ApplicationScoped
@Transactional
public class EstudianteService {

    @Inject
    private EstudianteRepository estudianteRepository;

    public List<EstudianteRepresentation> listarTodos() {
        List<EstudianteRepresentation> listaR = new ArrayList<>();
        List<Estudiante> lista = this.estudianteRepository.listAll();
        for (Estudiante est : lista) {
            listaR.add(mapperToER(est));
        }
        return listaR;
    }

    public EstudianteRepresentation consultarPorId(Integer id) {
        // Usamos longValue() para coincidir con el tipo esperado por Panache si el ID es Integer
        Estudiante est = this.estudianteRepository.findById(id.longValue());
        if (est != null) {
            return mapperToER(est);
        }
        return null;
    }

    @Transactional
    public void crear(EstudianteRepresentation estuR) {
        Estudiante est = this.mapperToEstudiante(estuR);
        this.estudianteRepository.persist(est);
    }

    @Transactional
    public void actualizar(EstudianteRepresentation estuR, Integer id) {
        Estudiante est = this.estudianteRepository.findById(id.longValue());
        if (est != null) {
            est.cedula = estuR.cedula;
            est.nombre = estuR.nombre;
            est.apellido = estuR.apellido;
            est.email = estuR.email;
            // La edad no se actualiza porque no viene en la Representation
        }
    }

    @Transactional
    public void actualizarParcial(EstudianteRepresentation estuR, Integer id) {
        Estudiante est = this.estudianteRepository.findById(id.longValue());
        if (est != null) {
            if (estuR.cedula != null) est.cedula = estuR.cedula;
            if (estuR.nombre != null) est.nombre = estuR.nombre;
            if (estuR.apellido != null) est.apellido = estuR.apellido;
            if (estuR.email != null) est.email = estuR.email;
        }
    }

    @Transactional
    public void borrar(Integer id) {
        this.estudianteRepository.deleteById(id.longValue());
    }

   
    // Convierte de Entidad a Representación (DTO)
    public EstudianteRepresentation mapperToER(Estudiante est) {
        EstudianteRepresentation er = new EstudianteRepresentation();
        er.id = est.id;
        er.cedula = est.cedula;
        er.nombre = est.nombre;
        er.apellido = est.apellido;
        er.email = est.email;
        return er;
    }

    
    // Convierte de Representación a Entidad
    public Estudiante mapperToEstudiante(EstudianteRepresentation er) {
        Estudiante est = new Estudiante();
        est.id = er.id;
        est.cedula = er.cedula;
        est.nombre = er.nombre;
        est.apellido = er.apellido;
        est.email = er.email;
        // La edad queda por defecto ya que no está en la representación
        return est;
    }
}