package consultorio.presentation.login;

import consultorio.logic.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import consultorio.logic.ConsultorioService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.stereotype.Controller("login")
public class Controller {
    @Autowired
    private ConsultorioService service;

    @GetMapping("/presentation/login/show")
    public String show() {
        return "presentation/login/View"; // Apunta a tu plantilla HTML de login
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {
        // Verificar las credenciales
        if (service.autenticar(username, password)) {
            // Guardar información en la sesión
            Usuario usuario = service.buscarPorUsername(username);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("username", usuario.getNombre());

            // Redirigir a página principal después del login
            return "/presentation/login/success";
        } else {
            // Agregar mensaje de error y volver al formulario de login
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "presentation/login/View";
        }
    }

//    @GetMapping("/logout")
//    public String logout(HttpSession session) {
//        // Invalidar la sesión al cerrar sesión
//        session.invalidate();
//        return "redirect:/login/View";
//    }

//    @GetMapping("/presentation/login/success")
//    public String dashboard(HttpSession session, Model model) {
//        // Verificar si el usuario está autenticado
//        if (session.getAttribute("usuarioId") == null) {
//            return "redirect:presentation";
//        }
//
//        // Agregar información del usuario al modelo
//        model.addAttribute("username", session.getAttribute("username"));
//        return "presentation/login/View";
//    }




}