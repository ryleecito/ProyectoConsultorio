package consultorio.data;

import consultorio.logic.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SlotsRepository extends JpaRepository<Slot, Integer> {
}
