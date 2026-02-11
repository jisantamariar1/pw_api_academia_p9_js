package uce.edu.web.api.infraestructure;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import uce.edu.web.api.domain.Estudiante;
@ApplicationScoped
public class EstudianteRepository implements PanacheRepository<Estudiante>{

}
