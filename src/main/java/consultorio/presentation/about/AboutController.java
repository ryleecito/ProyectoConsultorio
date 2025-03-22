package consultorio.presentation.about;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/presentation/about")
public class AboutController {

    @GetMapping("/show")
    public String about() {
        return "presentation/about/about"; // Indicamos la ruta completa dentro de templates/
    }
}
