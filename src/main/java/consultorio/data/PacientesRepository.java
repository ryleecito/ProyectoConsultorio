package consultorio.data;


import consultorio.Paciente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacientesRepository extends CrudRepository<Paciente, Integer> {
}
