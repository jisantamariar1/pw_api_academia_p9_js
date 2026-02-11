package uce.edu.web.api.domain;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlRootElement;
@XmlRootElement
@Entity
@Table(name="Estudiante")
@SequenceGenerator(name ="estudiante_seq",sequenceName = "estudiante_secuencia",allocationSize = 1 )
public class Estudiante extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estudiante_seq")
    public Integer id;
    public String cedula;
    public String nombre;
    public String apellido;
    public String email;
    public int edad;
    //relacion con cursos muchos a muchos
    // @ManyToMany(cascade = CascadeType.ALL)
    // @JoinTable(
    //     name="matricula",
    //     joinColumns = @JoinColumn(name="estudiante_id"),
    //     inverseJoinColumns = @JoinColumn(name="curso_id")
    // )
    // public List<Curso> cursos;
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonbTransient
    public List<Inscripcion> inscripciones = new ArrayList<>();

    public Estudiante(){}

}
