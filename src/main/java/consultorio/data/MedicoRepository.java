package consultorio.data;

import consultorio.logic.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, String>{
    List<Medico> findByCiudad(String ciudad);
    List<Medico> findByEspecialidad(String especialidad);
    List<Medico> findByEspecialidadAndCiudad(String especialidad, String ciudad);
    Optional<Medico> findById(String id);

    @Query("SELECT m FROM Medico m LEFT JOIN FETCH m.slots WHERE m.id = :id")
    Medico findByIdWithSlots(@Param("id") String id);
}
