package consultorio.presentation.pacientes;

import org.springframework.beans.factory.annotation.Autowired;
import consultorio.logic.ConsultorioService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller("pacientes")
@RequestMapping("/presentation/pacientes")
public class PacientesController {
    @Autowired
    private ConsultorioService service;


    @GetMapping("/show")
    public String show(Model model) {
        model.addAttribute("pacientes",service.pacientesFindAll());
        return "presentation/pacientes/View";
    }

}
