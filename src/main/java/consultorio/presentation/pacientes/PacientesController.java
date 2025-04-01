package consultorio.presentation.pacientes;

import consultorio.logic.Cita;
import consultorio.logic.Medico;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import consultorio.logic.ConsultorioService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Controller("pacientes")
@RequestMapping("/presentation/pacientes")
public class PacientesController {
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

        model.addAttribute("pacientes",service.pacientesFindAll());
        model.addAttribute("citasList", service.buscarCitasIdMedico(usuarioId));

        return "presentation/pacientes/View";
    }

    @PostMapping("/search")
    public String search(
        @ModelAttribute("citasSearch") Cita citasSearch,
        Model model,
        @RequestParam("orden") String orden,
        @RequestParam("paciente") String paciente,
        HttpSession session
    ) {
        List<Cita> resultados = service.citasSearch(citasSearch.getEstado(), orden, paciente, (String) session.getAttribute("usuarioId"));

        if (resultados == null) {
            resultados = new ArrayList<>();
        }

        model.addAttribute("citasList", resultados);
        model.addAttribute("citasSearch", citasSearch);
        model.addAttribute("orden", orden);
        model.addAttribute("paciente", paciente);

        return "presentation/pacientes/View";
    }


    @GetMapping("/atender")
    public String attend(
        @RequestParam("citaId") String citaId
    ) {
        service.citaAttend(citaId);
        return "redirect:/presentation/pacientes/show";
    }

    @GetMapping("/cancelar")
    public String cancel(
            @RequestParam("citaId") String citaId
    ) {
        service.citaCancel(citaId);
        return "redirect:/presentation/pacientes/show";
    }

    @GetMapping("/observaciones")
    public String observaciones(@RequestParam("citaId") String citaId, Model model) {
        Cita cita = service.buscarCitaPorId(citaId);
        model.addAttribute("cita", cita);
        return "presentation/observaciones/View";
    }

    @PostMapping("/guardarObservaciones")
    public String guardarObservaciones(@RequestParam("citaId") String citaId,
                                       @RequestParam("observaciones") String observaciones) {
        service.guardarObservacionesCita(citaId, observaciones);
        return "redirect:/presentation/pacientes/show";
    }

}
