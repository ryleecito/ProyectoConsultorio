package consultorio.presentation.medicos;

import consultorio.logic.Cita;
import consultorio.logic.ConsultorioService;
import consultorio.logic.Medico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@org.springframework.stereotype.Controller
@RequestMapping("/presentation/medicos")
public class MedicosController {

    @Autowired
    private ConsultorioService service;

    @ModelAttribute("medicosSearch")
    public Medico medicosSearch() {
        Medico medicoSearch = new Medico();
        medicoSearch.setEspecialidad("");
        medicoSearch.setCiudad("");
        return medicoSearch;
    }

    @GetMapping("/show")
    public String show() {
        return "redirect:/presentation/medicos/list";
    }

    @ModelAttribute("citas")
    public List<Cita> citas() {
        return new ArrayList<>();
    }

    @PostMapping("/search")
    public String search(@ModelAttribute("medicosSearch") Medico medicoSearch, Model model) {
        List<Medico> resultados = service.medicoSearch(medicoSearch.getEspecialidad(), medicoSearch.getCiudad());

        if (resultados == null) {
            resultados = new ArrayList<>();
        }


        prepararDatosMedicos(resultados, model, null);

        return "presentation/medicos/View";
    }


    @GetMapping("/list")
    public String medicoList(Authentication authentication, Model model, @ModelAttribute("medicosSearch") Medico medicoSearch) {
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean isMedicoOrAdmin = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("MEDICO") || a.getAuthority().equals("ADMIN"));
            if (isMedicoOrAdmin) {
                return "redirect:/presentation/login/access-denied";
            }
        }
        List<Medico> resultados = service.medicoSearch(medicoSearch.getEspecialidad(), medicoSearch.getCiudad());

        if (resultados == null) {
            resultados = new ArrayList<>();
        }
        prepararDatosMedicos(resultados, model, null);


        return "/presentation/medicos/View";
    }


    private void prepararDatosMedicos(List<Medico> medicos, Model model, String semanaParam) {

        List<Medico> medicosFiltrados = new ArrayList<>();

        for (Medico medico : medicos) {
            if (medico != null &&
                    medico.getEmail() != null &&
                    !medico.getEmail().isEmpty() &&
                    medico.getSlots() != null &&
                    !medico.getSlots().isEmpty()) {

                medicosFiltrados.add(medico);
            }
        }


        LocalDate fechaInicio;


        Integer weekOffset = (Integer) model.getAttribute("weekOffset");
        if (weekOffset == null) {
            weekOffset = 0;
        }


        if (semanaParam != null && !semanaParam.isEmpty()) {
            try {
                fechaInicio = LocalDate.parse(semanaParam);
            } catch (Exception e) {
                fechaInicio = LocalDate.now();
            }
        } else {
            fechaInicio = LocalDate.now();
        }

        fechaInicio = fechaInicio.plusWeeks(weekOffset);


        LocalDate inicioSemana = fechaInicio.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime fechaInicioTime = inicioSemana.atStartOfDay();


        LocalDate semanaAnterior = inicioSemana.minusWeeks(1);
        LocalDate semanaSiguiente = inicioSemana.plusWeeks(1);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fechaInicioFormateada = inicioSemana.format(formatter);

        Map<String, List<List<Cita>>> citasPorMedico = new HashMap<>();


        for (Medico medico : medicosFiltrados) {

            List<List<Cita>> citasDeLaSemana = medico.obtenerCitasDeLaSemana(inicioSemana);


            for (List<Cita> citasDelDia : citasDeLaSemana) {
                for (Cita cita : citasDelDia) {


                        Cita citaExistente = service.findCitaByFechaAndMedicoId(cita.getFecha(), medico.getId());

                        if (citaExistente != null) {
                            cita.setEstado("Pendiente");
                        }

                }
            }

            citasPorMedico.put(medico.getId(), citasDeLaSemana);
        }


        model.addAttribute("medicoSearch", medicosFiltrados);
        model.addAttribute("citasPorMedico", citasPorMedico);
        model.addAttribute("fechaInicio", fechaInicioTime);
        model.addAttribute("fechaInicioFormateada", fechaInicioFormateada);
        model.addAttribute("semanaAnterior", semanaAnterior.format(formatter));
        model.addAttribute("semanaSiguiente", semanaSiguiente.format(formatter));


        model.addAttribute("weekOffset", weekOffset);
    }

    @GetMapping("/schedule/{medicoId}")
    public String showSchedule(@PathVariable String medicoId,
                               @RequestParam(required = false) String semana,
                               @RequestParam(required = false, defaultValue = "0") Integer weekOffset,
                               Model model) {

        Medico medico = service.buscarMedicoPorId(medicoId);

        if (medico == null) {

            return "redirect:/presentation/medicos/list";
        }


        model.addAttribute("weekOffset", weekOffset);


        List<Medico> medicosList = new ArrayList<>();
        medicosList.add(medico);


        prepararDatosMedicos(medicosList, model, semana);


        model.addAttribute("medicoSeleccionado", medico);
        model.addAttribute("medico", medico);

        return "presentation/medicos/schedule";
    }

    @GetMapping("/appointment-details")
    public String showAppointmentDetails(
            @RequestParam("medicoId") String medicoId,
            @RequestParam("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime fecha,
            Model model) {


        Medico medico = service.buscarMedicoPorId(medicoId);


        Cita cita = service.findCitaByFechaAndMedicoId(fecha, medicoId);


        if (cita == null) {
            cita = new Cita();
            cita.setMedico(medico);
            cita.setFecha(fecha);
            cita.setHoraInicio(fecha.toLocalTime());
            cita.setHoraFin(fecha.toLocalTime().plusMinutes(medico.getDuracionCita()));
            cita.setFechaCreacion(java.time.Instant.now());
            cita.setEstado("Disponible");
        }

        model.addAttribute("medico", medico);
        model.addAttribute("cita", cita);

        return "presentation/book/confirmar_cita";
    }
}