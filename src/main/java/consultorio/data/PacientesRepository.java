package consultorio.data;

import consultorio.logic.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacientesRepository extends JpaRepository<Paciente, String> {
    List<Paciente> findByDireccion(String direccion);
    Optional<Paciente> findById(String id);
    Optional<Paciente> findByEmail(String email);
}
