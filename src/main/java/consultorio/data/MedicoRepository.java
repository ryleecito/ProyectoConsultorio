package consultorio.data;

import consultorio.logic.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, String>{
    List<Medico> findByCiudad(String ciudad);

    List<Medico> findByEspecialidad(String especialidad);

    List<Medico> findByEspecialidadAndCiudad(String especialidad, String ciudad);
}
