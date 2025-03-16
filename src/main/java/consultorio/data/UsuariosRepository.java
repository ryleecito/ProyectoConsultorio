package consultorio.data;

import consultorio.Usuario;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, Integer> {
    Usuario findUsuarioById(@Size(max = 20) String id);
}