package consultorio.presentation.pacientes;

import org.springframework.beans.factory.annotation.Autowired;
import consultorio.logic.ConsultorioService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller("pacientes")
public class Controller {
    @Autowired
    private ConsultorioService service;

    @GetMapping("/presentation/pacientes/show")
    public String show(Model model) {
        model.addAttribute("pacientes",service.pacientesFindAll());
        return "presentation/pacientes/show";
    }

}
