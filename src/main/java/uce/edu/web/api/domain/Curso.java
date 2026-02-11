package uce.edu.web.api.domain;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "curso")
@SequenceGenerator(name = "curso_seq",sequenceName = "curso_secuencia", allocationSize = 1)
public class Curso extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "curso_seq")
    public Integer id;
    
    public String nombre;
    public String codigo;
    public String descripcion;
    public Integer cupos;

    public String estado;


    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    @JsonbTransient
    public List<Inscripcion> inscripciones = new ArrayList<>();

    public Curso(){
        this.estado="ABIERTO";
    }


}
