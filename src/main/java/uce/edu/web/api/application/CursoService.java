package uce.edu.web.api.application;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import uce.edu.web.api.application.representation.CursoRepresentation;
import uce.edu.web.api.domain.Curso;
import uce.edu.web.api.infraestructure.CursoRepository;

@ApplicationScoped
@Transactional
public class CursoService {

    @Inject
    private CursoRepository cursoRepository;

    public List<CursoRepresentation> listarTodos(){
        List<CursoRepresentation> listaR = new ArrayList<>();
        List<Curso> lista = this.cursoRepository.listAll();
        for(Curso c : lista){
            listaR.add(mapperToCR(c));
        }
        return listaR;
    }

    public CursoRepresentation consultarPorId(Integer id){
        Curso curso = this.cursoRepository.findById(id.longValue());
        // Validación para evitar que el mapper explote si el curso no existe
        if (curso != null) {
            return mapperToCR(curso);
        }
        return null;
    }

    @Transactional
    public void crear(CursoRepresentation cursoR){
        Curso curso = this.mapperToCurso(cursoR);
        this.cursoRepository.persist(curso);
    }

    @Transactional
    public void actualizar(CursoRepresentation cursoR, Integer id){
        // Usamos el id directamente ya que es Integer en la Entidad
        Curso curso = this.cursoRepository.findById(id.longValue());
        if(curso != null){
            curso.codigo = cursoR.codigo;
            curso.cupos = cursoR.cupos;
            curso.descripcion = cursoR.descripcion;
            curso.estado = cursoR.estado;
            curso.nombre = cursoR.nombre;
        }
    }

    @Transactional
    public void actualizarParcial(CursoRepresentation cursoR, Integer id){
        Curso curso = this.cursoRepository.findById(id.longValue());
        if(curso != null){
            if(cursoR.codigo != null) curso.codigo = cursoR.codigo;
            if(cursoR.cupos != null) curso.cupos = cursoR.cupos;
            if(cursoR.descripcion != null) curso.descripcion = cursoR.descripcion;
            if(cursoR.estado != null) curso.estado = cursoR.estado;
            if(cursoR.nombre != null) curso.nombre = cursoR.nombre;
        }
    }

    @Transactional
    public void borrar(Integer id){
        this.cursoRepository.deleteById(id.longValue());
    }

    public CursoRepresentation mapperToCR(Curso curso){
        CursoRepresentation cursoR = new CursoRepresentation();
        cursoR.id = curso.id;
        cursoR.codigo = curso.codigo;
        cursoR.cupos = curso.cupos;
        cursoR.descripcion = curso.descripcion;
        cursoR.estado = curso.estado;
        cursoR.nombre = curso.nombre;
        return cursoR;
    }

    public Curso mapperToCurso(CursoRepresentation cursoR){
        Curso curso = new Curso();
        curso.id = cursoR.id;
        curso.codigo = cursoR.codigo;
        curso.cupos = cursoR.cupos;
        curso.descripcion = cursoR.descripcion;
        curso.estado = cursoR.estado;
        curso.nombre = cursoR.nombre;
        return curso;
    }
}