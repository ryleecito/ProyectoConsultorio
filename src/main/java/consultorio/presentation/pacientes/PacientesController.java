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


    private static final String SESSION_FILTER_ESTADO = "filterEstado";
    private static final String SESSION_FILTER_ORDEN = "filterOrden";
    private static final String SESSION_FILTER_PACIENTE = "filterPaciente";

    @ModelAttribute("citasSearch")
    public Cita citasSearch(HttpSession session) {
        Cita citasSearch = new Cita();


        String estado = (String) session.getAttribute(SESSION_FILTER_ESTADO);
        citasSearch.setEstado(estado != null ? estado : "");

        return citasSearch;
    }

    @GetMapping("/show")
    public String show(Model model, HttpSession session) {
        String usuarioId = (String) session.getAttribute("usuarioId");


        String estado = (String) session.getAttribute(SESSION_FILTER_ESTADO);
        String orden = (String) session.getAttribute(SESSION_FILTER_ORDEN);
        String paciente = (String) session.getAttribute(SESSION_FILTER_PACIENTE);

        List<Cita> citasList;


        if (estado != null || orden != null || paciente != null) {
            citasList = service.citasSearch(
                    estado != null ? estado : "",
                    orden != null ? orden : "",
                    paciente != null ? paciente : "",
                    usuarioId);
        } else {

            citasList = service.buscarCitasIdMedico(usuarioId);
        }

        model.addAttribute("pacientes", service.pacientesFindAll());
        model.addAttribute("citasList", citasList);

        Cita citasSearch = new Cita();
        citasSearch.setEstado(estado != null ? estado : "");
        model.addAttribute("citasSearch", citasSearch);
        model.addAttribute("orden", orden != null ? orden : "");
        model.addAttribute("paciente", paciente != null ? paciente : "");

        return "presentation/pacientes/View";
    }

    @PostMapping("/search")
    public String search(
            @ModelAttribute("citasSearch") Cita citasSearch,
            @RequestParam("orden") String orden,
            @RequestParam("paciente") String paciente,
            Model model,
            HttpSession session
    ) {
        String usuarioId = (String) session.getAttribute("usuarioId");


        session.setAttribute(SESSION_FILTER_ESTADO, citasSearch.getEstado());
        session.setAttribute(SESSION_FILTER_ORDEN, orden);
        session.setAttribute(SESSION_FILTER_PACIENTE, paciente);

        List<Cita> resultados = service.citasSearch(
                citasSearch.getEstado(),
                orden,
                paciente,
                usuarioId
        );

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
    public String guardarObservaciones(
            @RequestParam("citaId") String citaId,
            @RequestParam("observaciones") String observaciones
    ) {
        service.guardarObservacionesCita(citaId, observaciones);
        return "redirect:/presentation/pacientes/show";
    }


    @GetMapping("/clearFilters")
    public String clearFilters(HttpSession session) {
        session.removeAttribute(SESSION_FILTER_ESTADO);
        session.removeAttribute(SESSION_FILTER_ORDEN);
        session.removeAttribute(SESSION_FILTER_PACIENTE);
        return "redirect:/presentation/pacientes/show";
    }
}