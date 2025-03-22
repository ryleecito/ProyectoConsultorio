package consultorio.presentation.login;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/presentation/login")
public class LoginController {

    @Autowired
    private ConsultorioService service;

    @GetMapping("/show")
    public String showLogin(@RequestParam(value = "success", required = false) String successMessage,
                            @RequestParam(value = "errorMessage", required = false) String errorMessage,
                            HttpSession session, Model model) {
        // Agregar mensaje de éxito si viene en la URL
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }

        // Agregar mensaje de error si viene en la URL
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        } else {
            // Eliminar mensaje de error de la sesión si no viene en la URL
            session.removeAttribute("errorMessage");
        }

        return "presentation/login/View";
    }


}
