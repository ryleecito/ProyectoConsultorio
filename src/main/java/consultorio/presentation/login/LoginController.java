package consultorio.presentation.login;

import consultorio.logic.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import consultorio.logic.ConsultorioService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
public class LoginController {

    @Autowired
    private ConsultorioService service;

    @GetMapping("/presentation/login/show")
    public String showLogin(@RequestParam(value = "success", required = false) String successMessage,
                            HttpSession session, Model model) {
        // ✅ Agregar mensaje de éxito si viene en la URL
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }

        // ✅ Eliminar mensaje de error cuando se recarga la página
        if (session.getAttribute("error") != null) {
            session.removeAttribute("error");
        }

        return "presentation/login/View";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (service.autenticar(username, password)) {
            Usuario usuario = service.buscarPorUsername(username);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("username", usuario.getNombre());
            session.setAttribute("rol", usuario.getRol());

            if (Objects.equals(usuario.getRol(), "ADMIN")) {
                return "redirect:/admin/medicos-pendientes";
            } else if (Objects.equals(usuario.getRol(), "MEDICO")) {
                return "redirect:/presentation/medicos/list";
            } else {
                return "redirect:/presentation/medicos/list";
            }
        } else {
            // ✅ Cambia el nombre del atributo a "errorMessage" para que coincida con Thymeleaf
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario o contraseña incorrectos");
            return "redirect:/presentation/login/show";
        }
    }
}
