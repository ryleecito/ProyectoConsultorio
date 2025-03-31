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

    List<Cita> findByFecha(@NotNull LocalDateTime fecha);

    Cita findByFechaAndMedicoId(LocalDateTime fechaCita, String medicoId);

    List<Cita> findByEstadoOrderByFechaDesc(String estado);

    List<Cita> findAllByOrderByFechaDesc();

    List<Cita> findAllByOrderByFechaAsc();

    List<Cita> findByEstado(String estado);

    List<Cita> findByEstadoOrderByFechaAsc(String estado);

    List<Cita> findByPacienteUsuarioNombreOrderByFechaDesc(String paciente);

    List<Cita> findByPacienteUsuarioNombreOrderByFechaAsc(String paciente);

    List<Cita> findByEstadoAndPacienteUsuarioNombre(String estado, String paciente);

    List<Cita> findByEstadoAndPacienteUsuarioNombreOrderByFechaDesc(String estado, String paciente);

    List<Cita> findByEstadoAndPacienteUsuarioNombreOrderByFechaAsc(String estado, String paciente);

    List<Cita> findByEstadoAndMedicoId(String estado, String medico);

    List<Cita> findByMedicoIdOrderByFechaAsc(String usuarioId);

    List<Cita> findByMedicoUsuarioNombre(String medico);

    List<Cita> findByMedicoUsuarioNombreContainingIgnoreCase(String medico);

    List<Cita> findByEstadoAndMedicoUsuarioNombreContainingIgnoreCase(String estado, String medico);

    List<Cita> findByPacienteUsuarioNombreContainingIgnoreCaseOrderByFechaDesc(String paciente);

    List<Cita> findByPacienteUsuarioNombreContainingIgnoreCaseOrderByFechaAsc(String paciente);

    List<Cita> findByEstadoAndPacienteUsuarioNombreContainingIgnoreCase(String estado, String paciente);

    List<Cita> findByEstadoAndPacienteUsuarioNombreContainingIgnoreCaseOrderByFechaDesc(String estado, String paciente);

    List<Cita> findByEstadoAndPacienteUsuarioNombreContainingIgnoreCaseOrderByFechaAsc(String estado, String paciente);
}
