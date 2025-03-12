package consultorio.logic;

import consultorio.data.PacientesRepository;
import consultorio.data.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsultorioService {
    @Autowired
    private PacientesRepository pacientesRepository;

    @Autowired
    private UsuariosRepository usuarioRepository;

    public Iterable<Paciente> pacientesFindAll() {
        return pacientesRepository.findAll();
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findUsuarioById(username);
    }

    public boolean autenticar(String username, String password) {
        Usuario usuario = buscarPorUsername(username);
        if (usuario == null) {
            return false;
        }
        return usuario.getPassword().equals(password);
    }
}
