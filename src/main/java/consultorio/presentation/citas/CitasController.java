package consultorio.presentation.citas;

import consultorio.logic.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@SessionAttributes ({"citasSearch"})
@RequestMapping("/presentation/citas")

public class CitasController {

    @Autowired
    private ConsultorioService service;

    @GetMapping("/show")
    public String showAppointments(Model model) {

        return "/presentation/citas/View";
    }

    @PostMapping("/confirm")
    public String confirmAppointment(
            @RequestParam("fechaCita") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime fechaCita,
            @RequestParam("medicoId") String medicoId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Medico medico = service.buscarMedicoPorId(medicoId);
            if (medico == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "No se encontró el médico seleccionado.");
                return "redirect:/presentation/medicos/list";
            }

            String usuarioId = (String) session.getAttribute("usuarioId");
            if (usuarioId == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Debe iniciar sesión para agendar una cita.");
                return "redirect:/presentation/login";
            }

            Paciente paciente = service.buscarPacientePorId(usuarioId);

            Cita cita = service.findCitaByFechaAndMedicoId(fechaCita, medicoId);
            if (cita == null) {
                cita = new Cita();
                cita.setMedico(medico);
                cita.setFecha(fechaCita);
                cita.setHoraInicio(fechaCita.toLocalTime());
                cita.setHoraFin(fechaCita.toLocalTime().plusMinutes(medico.getDuracionCita()));
                cita.setFechaCreacion(java.time.Instant.now());
            }

            cita.setPaciente(paciente);
            cita.setEstado("Pendiente");

            service.guardarCita(cita);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Cita agendada con éxito para el " +
                            fechaCita.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                            " a las " + fechaCita.format(DateTimeFormatter.ofPattern("HH:mm")) +
                            " con el Dr. " + medico.getUsuario().getNombre());

            return "redirect:/presentation/medicos/list";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al agendar la cita: " + e.getMessage());
            return "redirect:/presentation/medicos/list";
        }
    }
    @GetMapping("/list")
    public String listPacientes(Model model, HttpSession session) {
        String usuarioId = (String) session.getAttribute("usuarioId");
        String usuarioRol = (String) session.getAttribute("usuarioRol");
        if (usuarioRol.equals("MEDICO")) {
            List<Cita> citas = service.buscarCitasIdMedico(usuarioId);
            model.addAttribute("citasList", citas);
            return "presentation/pacientes/View";
        } else {
            model.addAttribute("citasList", service.buscarCitasIdPaciente(usuarioId));

            return "presentation/citas/show";
        }
    }

}
