package consultorio.logic;

import consultorio.data.CitasRepository;
import consultorio.data.MedicoRepository;
import consultorio.data.PacientesRepository;
import consultorio.data.UsuariosRepository;
import consultorio.logic.Cita;
import consultorio.logic.Medico;
import consultorio.logic.Paciente;
import consultorio.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultorioService {

    @Autowired
    private PacientesRepository pacientesRepository;

    @Autowired
    private UsuariosRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private CitasRepository citasRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // ✅ Inyectamos PasswordEncoder para encriptar contraseñas

    // ✅ Autenticación con comparación de contraseñas encriptadas
    public boolean autenticar(String id, String password) {
        Usuario usuario = buscarPorId(id);
        if (usuario == null) {
            return false;
        }
        return passwordEncoder.matches(password, usuario.getPassword());
    }

    // ✅ Método para guardar usuario con contraseña encriptada
    public void guardarUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); // ✅ Encriptamos la contraseña
        usuarioRepository.save(usuario);
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findUsuarioById(username);
    }

    public Iterable<Paciente> pacientesFindAll() {
        return pacientesRepository.findAll();
    }

    public void guardarMedico(Medico medico) {
        medicoRepository.save(medico);
    }

    public List<Usuario> obtenerMedicosPendientes() {
        return usuarioRepository.findByRolAndEstado("MEDICO", "PENDIENTE");
    }

    @Transactional
    public void aprobarMedico(String id) {
        Usuario usuarioMedico = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        usuarioMedico.setEstado("ACTIVO");
        usuarioRepository.updateEstado(usuarioMedico.getId(), usuarioMedico.getEstado());

        if (!medicoRepository.existsById(id)) {
            Medico medico = new Medico();
            medico.setId(id);
            medico.setCiudad("");
            medico.setEspecialidad("");
            medico.setCostoConsulta(BigDecimal.valueOf(0));
            medico.setDuracionCita(30);
            medico.setHospital("");
            medico.setFoto(null);
            guardarMedico(medico);
        }
    }

    public void rechazarMedico(String id) {
        usuarioRepository.deleteById(id);
    }

    public List<Medico> medicoSearch(String especialidad, String ciudad) {
        if ((especialidad == null || especialidad.isEmpty()) && (ciudad == null || ciudad.isEmpty())) {
            return medicoRepository.findAll();
        }

        if (especialidad == null || especialidad.isEmpty()) {
            return medicoRepository.findByCiudad(ciudad);
        }

        if (ciudad == null || ciudad.isEmpty()) {
            return medicoRepository.findByEspecialidad(especialidad);
        }

        return medicoRepository.findByEspecialidadAndCiudad(especialidad, ciudad);
    }

    public Medico buscarMedicoPorId(String id) {
        return medicoRepository.findById(id).orElse(null);
    }

    public void actualizarMedico(Medico medico) {
        medicoRepository.save(medico);
    }

    public Paciente buscarPacientePorId(String id) {
        return pacientesRepository.findById(id).orElse(null);
    }

    public void actualizarPaciente(Paciente paciente) {
        pacientesRepository.save(paciente);
    }

    public List<Cita> obtenerCitasPorMedico(String medicoId) {
        return citasRepository.findByMedicoId(medicoId);
    }

    public Optional<Medico> buscarMedicoPorNombre(String nombre) {
        return medicoRepository.findById(nombre);
    }

    public Usuario buscarPorId(String id) {
        return usuarioRepository.findById(id).orElse(null);
    }
}
