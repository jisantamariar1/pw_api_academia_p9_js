package uce.edu.web.api.domain;

import java.time.LocalDate;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
@Entity
@Table(name = "inscripcion")
@SequenceGenerator(name = "inscripcion_seq", sequenceName = "inscripcion_secuencia", allocationSize = 1)
public class Inscripcion extends PanacheEntityBase{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "inscripcion_seq")
    public Integer id;
    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    public Estudiante estudiante;
    @ManyToOne
    @JoinColumn(name = "curso_id")
    public Curso curso;

    public LocalDate fecha;
    public String estado;

    public Inscripcion(){
        this.fecha=LocalDate.now();
        this.estado="ACTIVA";
    }


}
