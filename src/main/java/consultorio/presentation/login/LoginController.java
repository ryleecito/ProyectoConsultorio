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

@Controller
public class LoginController {

    @Autowired
    private ConsultorioService service;

    @GetMapping("/presentation/login/show")
    public String showLogin(@RequestParam(value = "success", required = false) String successMessage, Model model) {
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }
        return "presentation/login/View";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {
        if (service.autenticar(username, password)) {
            Usuario usuario = service.buscarPorUsername(username);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("username", usuario.getNombre());

            // ✅ Pasar mensaje como parámetro en la redirección
            return "redirect:/presentation/citas/View?success=Has iniciado sesion correctamente";
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "presentation/login/View";
        }
    }
}