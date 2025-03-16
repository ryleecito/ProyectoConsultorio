package consultorio.presentation.register;

import consultorio.logic.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import consultorio.logic.ConsultorioService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;

@Controller
public class RegisterController {

    @Autowired
    private ConsultorioService service;

    @GetMapping("/presentation/register/show")
    public String showRegister() {
        return "presentation/register/View";
    }

    @PostMapping("/register")
    public String procesarRegistro(@RequestParam String id,
                                   @RequestParam String password,
                                   @RequestParam String nombre,
                                   @RequestParam String email,
                                   @RequestParam String rol,
                                   HttpSession session,
                                   Model model) {
        if (service.buscarPorUsername(id) != null) {
            model.addAttribute("error", "El usuario ya existe.");
            return "presentation/register/View";
        }

        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setPassword(password);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setRol(rol.equalsIgnoreCase("Medico") ? "MEDICO" : "PACIENTE");

        // 🔹 Establecer estado en PENDIENTE si es médico, ACTIVO si es paciente
        usuario.setEstado(rol.equalsIgnoreCase("Medico") ? "PENDIENTE" : "ACTIVO");

        usuario.setFechaRegistro(java.time.Instant.now());

        service.guardarUsuario(usuario);

        if (usuario.getRol().equalsIgnoreCase("MEDICO")) {
            return "redirect:/presentation/login/show?success=Te has registrado correctamente, esperando aprobación del administrador";
        } else {
            return "redirect:/presentation/login/show?success=Te has registrado correctamente";
        }
    }
}
