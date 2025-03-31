package consultorio.presentation.historial;

import consultorio.logic.Cita;
import consultorio.logic.Medico;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import consultorio.logic.ConsultorioService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Controller("historial")
@RequestMapping("/presentation/historial")
public class HistorialController {
    @Autowired
    private ConsultorioService service;


    @ModelAttribute("citasSearch")
    public Cita citasSearch() {
        Cita citasSearch = new Cita();
        citasSearch.setEstado("");

        return citasSearch;
    }


    @GetMapping("/show")
    public String show(Model model, HttpSession session) {
        String usuarioId = (String) session.getAttribute("usuarioId");
        model.addAttribute("citasList", service.citasFindAll());
        return "presentation/historial/View";
    }

    @PostMapping("/search")
    public String search(
            @ModelAttribute("citasSearch") Cita citasSearch,
            Model model,
            @RequestParam("medico") String medico
    ) {
        List<Cita> resultados = service.citasSearchMedico(citasSearch.getEstado(), medico);

        if (resultados == null) {
            resultados = new ArrayList<>();
        }

        model.addAttribute("citasList", resultados);
        model.addAttribute("medico", medico);

        return "presentation/historial/View";
    }



}
