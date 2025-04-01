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
    public Cita citasSearch(HttpSession session) {
        Cita citasSearch = new Cita();

        String filtroEstado = (String) session.getAttribute("filtroEstado");
        citasSearch.setEstado(filtroEstado != null ? filtroEstado : "");

        return citasSearch;
    }

    @GetMapping("/show")
    public String show(Model model, HttpSession session) {
        String usuarioId = (String) session.getAttribute("usuarioId");


        String filtroEstado = (String) session.getAttribute("filtroEstado");
        String filtroMedico = (String) session.getAttribute("filtroMedico");

        List<Cita> resultados;

        if (filtroEstado != null || filtroMedico != null) {

            String estado = filtroEstado != null ? filtroEstado : "";
            String medico = filtroMedico != null ? filtroMedico : "";

            resultados = service.citasSearchMedico(estado, medico, usuarioId);

            Cita citasSearch = citasSearch(session);
            citasSearch.setEstado(estado);
            model.addAttribute("citasSearch", citasSearch);

            model.addAttribute("medico", medico);
        } else {

            resultados = service.citasFindAllByUsuarioNombre(usuarioId);
        }

        if (resultados == null) {
            resultados = new ArrayList<>();
        }

        model.addAttribute("citasList", resultados);
        return "presentation/historial/View";
    }

    @PostMapping("/search")
    public String search(
            @ModelAttribute("citasSearch") Cita citasSearch,
            Model model,
            @RequestParam("medico") String medico,
            HttpSession session
    ) {
        String usuarioId = (String) session.getAttribute("usuarioId");

        session.setAttribute("filtroEstado", citasSearch.getEstado());
        session.setAttribute("filtroMedico", medico);

        List<Cita> resultados = service.citasSearchMedico(citasSearch.getEstado(), medico, usuarioId);

        if (resultados == null) {
            resultados = new ArrayList<>();
        }
        model.addAttribute("citasList", resultados);
        model.addAttribute("medico", medico);

        return "presentation/historial/View";
    }
}