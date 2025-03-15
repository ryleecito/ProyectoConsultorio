package consultorio.presentation.citas;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CitasController { // Nombre corregido

    @GetMapping("/presentation/citas/View")
    public String mostrarCitas(Model model) {
        return "presentation/citas/View"; // Asegúrate de que coincide con el nombre exacto del archivo HTML
    }
}
