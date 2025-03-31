package consultorio.logic;

import consultorio.data.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
    public List<Cita> citasSearch(String estado, String orden, String paciente, String medico) {
        Sort sort = createSort(orden);

        if ((estado == null || estado.isEmpty()) && (paciente == null || paciente.isEmpty())) {
            return citasRepository.findByMedicoId(medico,sort);
        }

        if (estado == null || estado.isEmpty()) {
            return paciente == null || paciente.isEmpty() ?
                    citasRepository.findByMedicoId(medico, sort) :
                    citasRepository.findByMedicoIdAndPacienteUsuarioNombre(medico, paciente, sort);
        }

        if (paciente == null || paciente.isEmpty()) {
            return citasRepository.findByEstadoAndMedicoId(estado,medico, sort);
        }

        return citasRepository.findByEstadoAndPacienteUsuarioNombre(estado, paciente, sort);
    }

    public List<Cita> citasSearchMedico(String estado, String medico) {
        Sort sort = createSort("asc"); // Default to ascending order for medico search

        if ((estado == null || estado.isEmpty()) && (medico == null || medico.isEmpty())) {
            return citasRepository.findAll(sort);
        }

        if (estado == null || estado.isEmpty()) {
            return citasRepository.findByMedicoUsuarioNombre(medico, sort);
        }

        if (medico == null || medico.isEmpty()) {
            return citasRepository.findByEstado(estado, sort);
        }

        return citasRepository.findByEstadoAndMedicoId(estado, medico, sort);
    }

    private Sort createSort(String orden) {
        Sort.Direction direction = "desc".equalsIgnoreCase(orden) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        // Create a sort that orders by fecha first, then by hora
        return Sort.by(
                new Sort.Order(direction, "fecha"),
                new Sort.Order(direction, "horaInicio")
        );
    }

    public List<Cita> buscarCitasIdMedico(String usuarioId) {
        Sort sort = Sort.by(
                new Sort.Order(Sort.Direction.ASC, "fecha"),
                new Sort.Order(Sort.Direction.ASC, "horaInicio")
        );
        return citasRepository.findByMedicoId(usuarioId, sort);
    }
    public Object buscarCitasIdPaciente(String usuarioId) {
        return citasRepository.findByPacienteId(usuarioId);
    }

    public void citaAttend(String citaId) {
        Cita cita = citasRepository.findById(Integer.parseInt(citaId)).orElse(null);
        if (cita == null) {
            throw new IllegalArgumentException("Cita no encontrada");
        }
        cita.setEstado("Atendida");
        citasRepository.save(cita);
    }

    public void citaCancel(String citaId) {
        Cita cita = citasRepository.findById(Integer.parseInt(citaId)).orElse(null);
        if (cita == null) {
            throw new IllegalArgumentException("Cita no encontrada");
        }
        cita.setEstado("Cancelada");
        citasRepository.save(cita);
    }

    public Cita buscarCitaPorId(String citaId) {
        return citasRepository.findById(Integer.parseInt(citaId)).orElse(null);
    }

    public void guardarObservacionesCita(String citaId, String observaciones) {
        Cita cita = citasRepository.findById(Integer.parseInt(citaId)).orElse(null);
        if (cita == null) {
            throw new IllegalArgumentException("Cita no encontrada");
        }
        cita.setNotasMedico(observaciones);
        citasRepository.save(cita);
    }

    public List<Cita> citasFindAllByUsuarioNombre(String usuarioId) {
        Sort sort = Sort.by(
                new Sort.Order(Sort.Direction.ASC, "fecha"),
                new Sort.Order(Sort.Direction.ASC, "horaInicio")
        );
        return citasRepository.findByPacienteId(usuarioId, sort);
    }

    public Medico medicoEncontrarIdSlots(String usuarioId) {
        return medicoRepository.findByIdWithSlots(usuarioId);
    }


}