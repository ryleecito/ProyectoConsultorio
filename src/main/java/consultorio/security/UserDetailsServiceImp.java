package consultorio.security;

import consultorio.data.UsuariosRepository;
import consultorio.logic.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    private final UsuariosRepository usuariosRepository;
    private final HttpSession session;

    public UserDetailsServiceImp(UsuariosRepository usuariosRepository, HttpSession session) {
        this.usuariosRepository = usuariosRepository;
        this.session = session;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Usuario usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + id));

        // Guardar ID y rol en la sesión después de autenticar
        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("usuarioRol", usuario.getRol());
        session.setAttribute("usuarioEstado", usuario.getEstado());
        System.out.println("Rol del usuario autenticado: " + usuario.getRol()); // Debug
        System.out.println("ID: " + usuario.getId()); // Debug
        System.out.println("Estado: " + usuario.getEstado()); // Debug
        return new UsersDetailsImp(usuario);
    }
}
