package consultorio.data;

import consultorio.logic.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitasRepository extends JpaRepository<Cita, Integer> {
    List<Cita> findByMedicoId(String medicoId);
}
