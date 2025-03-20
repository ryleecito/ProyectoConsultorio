package consultorio.presentation.register;

import consultorio.data.PacientesRepository;
import consultorio.logic.Medico;
import consultorio.logic.Paciente;
import consultorio.logic.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import consultorio.logic.ConsultorioService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Controller
@RequestMapping("/presentation/register")
public class RegisterController {

    @Autowired
    private ConsultorioService service;
    @Autowired
    private PacientesRepository pacientesRepository;

    @GetMapping("/show")
    public String showRegister() {
        return "presentation/register/View";
    }

    @PostMapping("/process")
    public String procesarRegistro(@RequestParam String id,
                                   @RequestParam String password,
                                   @RequestParam String nombre,
                                   @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto,
                                   @RequestParam String rol,
                                   HttpSession session,
                                   Model model) throws IOException {
        if (service.buscarPorUsername(id) != null) {
            model.addAttribute("error", "El usuario ya existe.");
            return "presentation/register/View";
        }

        System.out.println(id);
        System.out.println(password);
        System.out.println(nombre);


        //Crear un nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setPassword(password);
        usuario.setNombre(nombre);
        usuario.setRol(rol.equalsIgnoreCase("Medico") ? "MEDICO" : "PACIENTE");

        // 🔹 Establecer estado en PENDIENTE si es médico, ACTIVO si es paciente
        usuario.setEstado(rol.equalsIgnoreCase("Medico") ? "PENDIENTE" : "ACTIVO");

        usuario.setFechaRegistro(java.time.Instant.now());

        String userId = (String) session.getAttribute("usuarioId");

        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            try {
                // Generate unique filename
                String fileName = userId + "_" + System.currentTimeMillis() + "_" +
                        Objects.requireNonNull(profilePhoto.getOriginalFilename()).replaceAll("\\s+", "_");

                // Define the path where the file will be saved
                String uploadDir = "C:\\Users\\Saul Francis\\Desktop\\images_consultorio";
                Path uploadPath = Paths.get(uploadDir);

                // Create directory if it doesn't exist
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Save the file
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(profilePhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Store the relative path in the database
                usuario.setFoto("/images_consultorio/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                // Handle error
            }
        }


        service.guardarUsuario(usuario);

        if(usuario.getRol().equals("PACIENTE")) {
            Paciente paciente = new Paciente();
            paciente.setId(id);
            paciente.setDireccion("");
            paciente.setTelefono("");
            paciente.setEmail("");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate fechaNacimiento = LocalDate.parse("01-01-2000", formatter);
            paciente.setFechaNacimiento(fechaNacimiento);
            paciente.setUsuario(usuario);
            pacientesRepository.save(paciente);
        }

        if (usuario.getRol().equalsIgnoreCase("MEDICO")) {
            return "redirect:/presentation/login/show?success=Te has registrado correctamente, esperando aprobacion del administrador";
        } else {
            return "redirect:/presentation/login/show?success=Te has registrado correctamente";
        }
    }
}
