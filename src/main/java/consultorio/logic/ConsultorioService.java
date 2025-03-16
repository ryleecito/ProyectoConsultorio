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

    public void guardarMedico(Medico medico){
        medicoRepository.save(medico);
    }

    public List<Usuario> obtenerMedicosPendientes() {
        return usuarioRepository.findByRolAndEstado("MEDICO", "PENDIENTE");
    }

    public void aprobarMedico(String id) {
        Usuario medico = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Médico no encontrado"));
        medico.setEstado("ACTIVO");
        usuarioRepository.save(medico);
    }

    public void rechazarMedico(String id) {
        usuarioRepository.deleteById(id);
    }

    public List<Medico> medicoSearch(String especialidad, String ciudad) {
        // If both parameters are empty, return all doctors
        if ((especialidad == null || especialidad.isEmpty()) &&
                (ciudad == null || ciudad.isEmpty())) {
            return medicoRepository.findAll();
        }

        // If only one parameter is empty
        if (especialidad == null || especialidad.isEmpty()) {
            return medicoRepository.findByCiudad(ciudad);
        }

        if (ciudad == null || ciudad.isEmpty()) {
            return medicoRepository.findByEspecialidad(especialidad);
        }

        // Both parameters have values
        return medicoRepository.findByEspecialidadAndCiudad(especialidad, ciudad);
    }
}

