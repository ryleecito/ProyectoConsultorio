package consultorio.logic;

import consultorio.data.MedicoRepository;
import consultorio.data.PacientesRepository;
import consultorio.data.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultorioService {
    @Autowired
    private PacientesRepository pacientesRepository;

    @Autowired
    private UsuariosRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;


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

    public void guardarUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerMedicosPendientes() {
        return medicoRepository.findByRolAndEstado("MEDICO", "PENDIENTE");
    }

    public void aprobarMedico(String id) {
        Usuario medico = medicoRepository.findById(id).orElseThrow(() -> new RuntimeException("Médico no encontrado"));
        medico.setEstado("ACTIVO");
        medicoRepository.save(medico);
    }

    public void rechazarMedico(String id) {
        medicoRepository.deleteById(id);
    }
}

