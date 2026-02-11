package uce.edu.web.api.application;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import uce.edu.web.api.application.representation.InscripcionRepresentation;
import uce.edu.web.api.domain.Curso;
import uce.edu.web.api.domain.Estudiante;
import uce.edu.web.api.domain.Inscripcion;
import uce.edu.web.api.infraestructure.CursoRepository;
import uce.edu.web.api.infraestructure.EstudianteRepository;
import uce.edu.web.api.infraestructure.InscripcionRepository;

@ApplicationScoped
public class InscripcionService {

    @Inject
    private InscripcionRepository inscripcionRepository;
    
    @Inject
    private EstudianteRepository estudianteRepository;
    
    @Inject
    private CursoRepository cursoRepository;

    // Acción: Matricular (Crea la relación y descuenta cupo)
    @Transactional
    public void matricular(Integer estudianteId, Integer cursoId) {
        Estudiante est = estudianteRepository.findById(estudianteId.longValue());
        Curso cur = cursoRepository.findById(cursoId.longValue());

        // Lógica de negocio: Solo matricular si existen ambos y hay cupo
        if (est != null && cur != null && cur.cupos > 0) {
            Inscripcion ins = new Inscripcion();
            ins.estudiante = est;
            ins.curso = cur;
            ins.estado = "ACTIVA"; // Estado inicial
            
            // Regla: Disminuir el cupo del curso
            cur.cupos--; 
            
            this.inscripcionRepository.persist(ins);
        }
    }

    // Acción: Cancelar (Borrado lógico y devuelve cupo)
    @Transactional
    public void cancelar(Integer idInscripcion) {
        Inscripcion ins = inscripcionRepository.findById(idInscripcion.longValue());
        if (ins != null && !"CANCELADA".equals(ins.estado)) {
            ins.estado = "CANCELADA";
            // Regla: Al cancelar, devolvemos el cupo al curso
            ins.curso.cupos++;
        }
    }

    // Acción: Consulta específica (Como hizo el profe con los hijos)
    public List<InscripcionRepresentation> buscarPorEstudiante(Integer idEstudiante) {
        List<InscripcionRepresentation> listaR = new ArrayList<>();
        // HQL simple para buscar por la propiedad del objeto relacionado
        List<Inscripcion> lista = this.inscripcionRepository.find("estudiante.id = ?1", idEstudiante).list();
        
        for (Inscripcion ins : lista) {
            listaR.add(this.mapperToIR(ins));
        }
        return listaR;
    }

    // Mapper privado para transformar la entidad al DTO de representación
    private InscripcionRepresentation mapperToIR(Inscripcion ins) {
        InscripcionRepresentation ir = new InscripcionRepresentation();
        ir.id = ins.id;
        ir.nombreEstudiante = ins.estudiante.nombre + " " + ins.estudiante.apellido;
        ir.nombreCurso = ins.curso.nombre;
        ir.fecha = ins.fecha;
        ir.estado = ins.estado;
        return ir;
    }
}