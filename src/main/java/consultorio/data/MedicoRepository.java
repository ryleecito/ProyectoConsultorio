package consultorio.data;

import consultorio.logic.Usuario;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicoRepository extends JpaRepository<Usuario, String> {
    List<Usuario> findByRolAndEstado(@NotNull String rol, @NotNull String estado);
}
