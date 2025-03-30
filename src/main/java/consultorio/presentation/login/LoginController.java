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
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        } else {
            session.removeAttribute("errorMessage");
        }
        return "presentation/login/View";
    }
}
