package consultorio.data;

import consultorio.Medico;
import consultorio.Usuario;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, String>{
    List<Medico> findMedicosByEspecialidadAndCiudad(String nombre);

}
