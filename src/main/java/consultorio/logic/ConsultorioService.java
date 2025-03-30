package consultorio.logic;

import consultorio.data.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;


    public boolean autenticar(String id, String password) {
        Usuario usuario = buscarPorId(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }
        return true;
    }

    public void guardarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (usuarioRepository.existsById(usuario.getId())) {
            throw new IllegalArgumentException("Ya existe un usuario con este ID");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
    }


    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findUsuarioById(username);
    }

    public Iterable<Paciente> pacientesFindAll() {
        return pacientesRepository.findAll();
    }

    public List<Usuario> obtenerMedicosPendientes() {
        return usuarioRepository.findByRolAndEstado("MEDICO", "PENDIENTE");
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void aprobarMedico(String id) {
        Usuario usuarioMedico = entityManager.find(Usuario.class, id);
        if (usuarioMedico == null) {
            throw new RuntimeException("Médico no encontrado");
        }
        usuarioMedico.setEstado("ACTIVO");

        Optional<Medico> medicoOptional = medicoRepository.findById(id);
        if (medicoOptional.isEmpty()) {
            Medico medico = new Medico();
            medico.setId(id);
            medico.setCiudad("PREDET");
            medico.setEspecialidad("PREDET");
            medico.setCostoConsulta(BigDecimal.valueOf(0));
            medico.setDuracionCita(30);
            medico.setHospital("PREDET");
            medico.setEmail("PREDET");
            medico.setUsuario(usuarioMedico);

            medicoRepository.save(medico);
        }

        entityManager.flush();
        entityManager.refresh(usuarioMedico);
    }

    public void rechazarMedico(String id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("El médico no existe");
        }
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

    @Transactional(readOnly = true)
    public Medico buscarMedicoPorId(String id) {
        Medico medico = medicoRepository.findById(id).orElse(null);
        if (medico != null) {
            medico.getSlots().size();
        }
        return medico;
    }

    public void actualizarMedico(Medico medico) {
        if (medico == null) {
            throw new IllegalArgumentException("El médico no puede ser nulo");
        }
        if (!medicoRepository.existsById(medico.getId())) {
            throw new IllegalArgumentException("El médico no existe");
        }
        medicoRepository.save(medico);
    }

    public Paciente buscarPacientePorId(String id) {
        return pacientesRepository.findById(id).orElse(null);
    }

    public void actualizarPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        if (!pacientesRepository.existsById(paciente.getId())) {
            throw new IllegalArgumentException("El paciente no existe");
        }
        pacientesRepository.save(paciente);
    }

    public Usuario buscarPorId(String id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Cita findCitaByFechaAndMedicoId(LocalDateTime fechaCita, String medicoId) {
        return citasRepository.findByFechaAndMedicoId(fechaCita, medicoId);
    }

    public void guardarCita(Cita cita) {
        if (cita == null) {
            throw new IllegalArgumentException("La cita no puede ser nula");
        }
        citasRepository.save(cita);
    }

    public List<Cita> citasSearch(String estado, String orden, String paciente) {
        if ((estado == null || estado.isEmpty()) && (orden == null || orden.isEmpty()) && (paciente == null || paciente.isEmpty())) {
            return citasRepository.findAll();
        }
        if (estado == null || estado.isEmpty()) {
            if ("desc".equalsIgnoreCase(orden)) {
                return paciente == null || paciente.isEmpty() ?
                        citasRepository.findAllByOrderByFechaDesc() :
                        citasRepository.findByPacienteUsuarioNombreOrderByFechaDesc(paciente);
            } else {
                return paciente == null || paciente.isEmpty() ?
                        citasRepository.findAllByOrderByFechaAsc() :
                        citasRepository.findByPacienteUsuarioNombreOrderByFechaAsc(paciente);
            }
        }
        if (orden == null || orden.isEmpty()) {
            return paciente == null || paciente.isEmpty() ?
                    citasRepository.findByEstado(estado) :
                    citasRepository.findByEstadoAndPacienteUsuarioNombre(estado, paciente);
        }
        if ("desc".equalsIgnoreCase(orden)) {
            return paciente == null || paciente.isEmpty() ?
                    citasRepository.findByEstadoOrderByFechaDesc(estado) :
                    citasRepository.findByEstadoAndPacienteUsuarioNombreOrderByFechaDesc(estado, paciente);
        } else {
            return paciente == null || paciente.isEmpty() ?
                    citasRepository.findByEstadoOrderByFechaAsc(estado) :
                    citasRepository.findByEstadoAndPacienteUsuarioNombreOrderByFechaAsc(estado, paciente);
        }
    }

    public List<Cita> buscarCitasIdMedico(String usuarioId) {
        return citasRepository.findByMedicoId(usuarioId);
    }

    public Object buscarCitasIdPaciente(String usuarioId) {
        return citasRepository.findByPacienteId(usuarioId);
    }
}