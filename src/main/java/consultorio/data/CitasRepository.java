package consultorio.data;

import consultorio.logic.Cita;
import consultorio.logic.Medico;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitasRepository extends JpaRepository<Cita, Integer> {
    List<Cita> findByMedicoId(String medicoId);

    List<Cita> findByPacienteId(String medicoId);

    List<Cita> findByFecha(@NotNull LocalDateTime fecha);

    Cita findByFechaAndMedicoId(LocalDateTime fechaCita, String medicoId);

    // Methods with Sort parameter
    List<Cita> findByEstado(String estado, Sort sort);
    List<Cita> findByPacienteUsuarioNombre(String paciente, Sort sort);
    List<Cita> findByEstadoAndPacienteUsuarioNombre(String estado, String paciente, Sort sort);
    List<Cita> findByMedicoUsuarioNombre(String medico, Sort sort);
    List<Cita> findByEstadoAndMedicoId(String estado, String medico, Sort sort);
    List<Cita> findByMedicoId(String usuarioId, Sort sort);
    List<Cita> findByPacienteId(String usuarioId, Sort sort);

    // Original methods without Sort for backward compatibility
    List<Cita> findByEstado(String estado);
    List<Cita> findByMedicoUsuarioNombre(String medico);


    List<Cita> findByMedicoIdAndPacienteUsuarioNombre(String medico, String paciente, Sort sort);
}