package consultorio.data;

import consultorio.logic.Usuario;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, String> {
    Usuario findUsuarioById(@Size(max = 20) String id);

    List<Usuario> findByRolAndEstado(@NotNull String rol, @NotNull String estado);
}