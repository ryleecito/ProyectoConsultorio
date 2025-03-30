package consultorio.data;

import consultorio.logic.Usuario;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, String> {

    Usuario findUsuarioById(@Size(max = 20) String id);

    List<Usuario> findByRolAndEstado(@NotNull String rol, @NotNull String estado);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.estado = :estado WHERE u.id = :id")
    void updateEstado(@Size(max = 20) String id, @NotNull String estado);

    Optional<Usuario> findById(String id);


}
