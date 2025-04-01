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


    Cita findByFechaAndMedicoId(LocalDateTime fechaCita, String medicoId);
    List<Cita> findByEstadoAndMedicoId(String estado, String medico, Sort sort);
    List<Cita> findByMedicoId(String usuarioId, Sort sort);
    List<Cita> findByPacienteId(String usuarioId, Sort sort);
    List<Cita> findByMedicoIdAndPacienteUsuarioNombreContainingIgnoreCase(String medico, String paciente, Sort sort);
    List<Cita> findByPacienteIdAndEstado(String paciente, String estado, Sort sort);
    List<Cita> findByMedicoIdAndEstadoAndPacienteUsuarioNombreContainingIgnoreCase(String medico, String estado, String paciente, Sort sort);
    List<Cita> findByPacienteIdAndMedicoUsuarioNombreContainingIgnoreCase(String paciente, String medico, Sort sort);
    List<Cita> findByPacienteIdAndEstadoAndMedicoIdContainingIgnoreCase(String paciente, String estado, String medico, Sort sort);

}