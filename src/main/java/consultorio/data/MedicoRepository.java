package consultorio.data;

import consultorio.logic.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, String>{
    List<Medico> findMedicosByEspecialidadAndCiudad(String especialidad, String ciudad);

}
