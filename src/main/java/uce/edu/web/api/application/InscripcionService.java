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

    // Acción: Matricular (Crea la relación, descuenta cupo y CIERRA si es necesario)
    @Transactional
    public void matricular(Integer estudianteId, Integer cursoId) {
        Estudiante est = estudianteRepository.findById(estudianteId.longValue());
        Curso cur = cursoRepository.findById(cursoId.longValue());

        // Validamos: que existan, que el curso tenga cupos y esté ABIERTO
        if (est != null && cur != null && cur.cupos > 0 && "ABIERTO".equals(cur.estado)) {
            Inscripcion ins = new Inscripcion();
            ins.estudiante = est;
            ins.curso = cur;
            ins.estado = "ACTIVA"; 
            
            // 1. Disminuir el cupo del curso
            cur.cupos--; 
            
            // 2. LÓGICA NUEVA: Si los cupos llegaron a 0, cerramos el curso
            if (cur.cupos == 0) {
                cur.estado = "CERRADO";
            }
            
            this.inscripcionRepository.persist(ins);
        }
    }

    // Acción: Cancelar (Borrado lógico, devuelve cupo y ABRE si estaba cerrado)
    @Transactional
    public void cancelar(Integer idInscripcion) {
        Inscripcion ins = inscripcionRepository.findById(idInscripcion.longValue());
        
        if (ins != null && !"CANCELADA".equals(ins.estado)) {
            ins.estado = "CANCELADA";
            
            // 1. Devolvemos el cupo al curso
            ins.curso.cupos++;

            // 2. LÓGICA NUEVA: Si el curso estaba CERRADO, ahora debe estar ABIERTO
            // porque acabamos de liberar un espacio.
            if ("CERRADO".equals(ins.curso.estado)) {
                ins.curso.estado = "ABIERTO";
            }
        }
    }

    // Acción: Consulta específica
    public List<InscripcionRepresentation> buscarPorEstudiante(Integer idEstudiante) {
        List<InscripcionRepresentation> listaR = new ArrayList<>();
        List<Inscripcion> lista = this.inscripcionRepository.find("estudiante.id = ?1", idEstudiante).list();
        
        for (Inscripcion ins : lista) {
            listaR.add(this.mapperToIR(ins));
        }
        return listaR;
    }

    private InscripcionRepresentation mapperToIR(Inscripcion ins) {
        InscripcionRepresentation ir = new InscripcionRepresentation();
        ir.id = ins.id;
        if(ins.estudiante != null) {
             ir.nombreEstudiante = ins.estudiante.nombre + " " + ins.estudiante.apellido;
        }
        if(ins.curso != null) {
             ir.nombreCurso = ins.curso.nombre;
        }
        ir.fecha = ins.fecha;
        ir.estado = ins.estado;
        return ir;
    }
}