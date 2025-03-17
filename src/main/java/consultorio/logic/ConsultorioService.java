package consultorio.logic;

import consultorio.data.MedicoRepository;
import consultorio.data.PacientesRepository;
import consultorio.data.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Medico buscarMedicoPorId(String id) {
        return medicoRepository.findById(id).orElse(null);
    }
    public void actualizarMedico(Medico medico) {
        medicoRepository.save(medico);
    }

    // MÉTODOS PARA PACIENTES:
    public Paciente buscarPacientePorId(String id) {
        return pacientesRepository.findById(id).orElse(null);
    }

    public void actualizarPaciente(Paciente paciente) {
        pacientesRepository.save(paciente);
    }






    public Optional<Medico> buscarMedicoPorNombre(String nombre) {
        return medicoRepository.findById(nombre);
    }
}

