package consultorio.data;

import consultorio.logic.Cita;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitasRepository extends JpaRepository<Cita, Integer> {
    List<Cita> findByMedicoId(String medicoId);

    List<Cita> findByPacienteId(String medicoId);
    // Corrected method name to follow JPA naming convention
    List<Cita> findByFecha(@NotNull LocalDateTime fecha);

    Cita findByFechaAndMedicoId(LocalDateTime fechaCita, String medicoId);
}
