package consultorio.presentation.register;

import consultorio.data.PacientesRepository;
import consultorio.logic.Paciente;
import consultorio.logic.Usuario;
import consultorio.logic.ConsultorioService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequestMapping("/presentation/register")
public class RegisterController {

    @Autowired
    private ConsultorioService service;

    @Autowired
    private PacientesRepository pacientesRepository;

    @Value("${picturesPath}")
    private String picturesPath;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/show")
    public String showRegister(Model model) {
        // Agregamos un objeto Usuario para el binding en la vista.
        if (!model.containsAttribute("usuario")) {
            System.out.println("DEBUG: Agregando nuevo objeto Usuario al modelo");
            Usuario usuario = new Usuario();
            usuario.setEstado("TEMP"); // Valor temporal para pasar la validación
            usuario.setFoto("TEMP");   // Valor temporal
            usuario.setRol("TEMP");    // Valor temporal
            model.addAttribute("usuario", usuario);
        }
        System.out.println("DEBUG: Mostrando vista de registro");
        return "presentation/register/View";
    }

    @PostMapping("/process")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String procesarRegistro(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            @RequestParam String rolSeleccionado,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            Model model) throws IOException {

        System.out.println("DEBUG: Iniciando proceso de registro para id: " + usuario.getId());

        // Asignar rol y estado basados en el parámetro "rolSeleccionado"
        if (rolSeleccionado.equalsIgnoreCase("Medico")) {
            usuario.setRol("MEDICO");
            usuario.setEstado("PENDIENTE");
        } else {
            usuario.setRol("PACIENTE");
            usuario.setEstado("ACTIVO");
        }

        // Validación manual: la foto es obligatoria.
        if (profilePhoto == null || profilePhoto.isEmpty()) {
            result.rejectValue("foto", "NotNull.usuario.foto", "La foto no puede ser nula");
        } else {
            String originalFilename = profilePhoto.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = usuario.getId() + fileExtension;
            usuario.setFoto("/image/" + fileName); // Se asigna temporalmente para pasar la validación
        }

        if (result.hasErrors()) {
            System.out.println("DEBUG: Errores de validación encontrados: " + result.getAllErrors());
            return "presentation/register/View";
        }

        // Verificar si ya existe un usuario con ese ID
        if (service.buscarPorUsername(usuario.getId()) != null) {
            model.addAttribute("error", "El usuario ya existe.");
            return "presentation/register/View";
        }

        if (!usuario.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "presentation/register/View";
        }

        System.out.println("DEBUG: id = " + usuario.getId());
        System.out.println("DEBUG: password = " + usuario.getPassword());
        System.out.println("DEBUG: nombre = " + usuario.getNombre());
        System.out.println("DEBUG: rol = " + usuario.getRol());
        System.out.println("DEBUG: estado = " + usuario.getEstado());

        usuario.setFechaRegistro(java.time.Instant.now());

        // Procesar la foto de perfil
        try {
            String originalFilename = profilePhoto.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = usuario.getId() + fileExtension;
            Path uploadPath = Paths.get(picturesPath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(profilePhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            usuario.setFoto("/image/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al subir la imagen de perfil");
            return "presentation/register/View";
        }

        // Guardar el usuario (la contraseña se codifica en el service)
        service.guardarUsuario(usuario);

        // Si el rol es PACIENTE, crear el registro en la tabla de pacientes.
        if (usuario.getRol().equals("PACIENTE")) {
            Usuario usuarioPaciente = entityManager.find(Usuario.class, usuario.getId());
            if (usuarioPaciente == null) {
                throw new RuntimeException("Paciente no encontrado");
            }
            Optional<Paciente> pacienteOptional = pacientesRepository.findById(usuario.getId());
            if (pacienteOptional.isEmpty()) {
                Paciente paciente = new Paciente();
                paciente.setId(usuarioPaciente.getId());
                paciente.setDireccion("PREDET");
                paciente.setTelefono("PREDET");
                paciente.setEmail("PREDET");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate fechaNacimiento = LocalDate.parse("01-01-2000", formatter);
                paciente.setFechaNacimiento(fechaNacimiento);
                paciente.setUsuario(usuarioPaciente);
                pacientesRepository.save(paciente);
            }
            entityManager.flush();
            entityManager.refresh(usuarioPaciente);
        }

        if (usuario.getRol().equalsIgnoreCase("MEDICO")) {
            return "redirect:/presentation/login/show?success=Te has registrado correctamente, esperando aprobacion del administrador";
        } else {
            return "redirect:/presentation/login/show?success=Te has registrado correctamente";
        }
    }
}
