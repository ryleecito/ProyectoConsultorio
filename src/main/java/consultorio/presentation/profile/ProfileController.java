package consultorio.presentation.profile;

import consultorio.logic.Medico;
import consultorio.logic.ConsultorioService;
import consultorio.logic.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ConsultorioService consultorioService;

    // ======================== MÉDICO ========================

    @GetMapping("/medico")
    public String profileMedico(Model model, HttpSession session) {
        String medicoId = (String) session.getAttribute("usuarioId");
        if (medicoId == null) return "redirect:/presentation/login/show";

        Medico medico = consultorioService.buscarMedicoPorId(medicoId);
        if (medico == null) return "redirect:/presentation/login/show";

        model.addAttribute("medico", medico);
        return "presentation/profile/profile";
    }

    @PostMapping("/medico/update")
    public String updateMedico(
            @RequestParam("especialidad") String especialidad,
            @RequestParam("ciudad") String ciudad,
            @RequestParam("costo_consulta") Double costoConsulta,
            @RequestParam("duracion_cita") Integer duracionCita,
            @RequestParam("hospital") String hospital,
            HttpSession session) {

        String medicoId = (String) session.getAttribute("usuarioId");
        if (medicoId == null) return "redirect:/presentation/login/show";

        Medico medico = consultorioService.buscarMedicoPorId(medicoId);
        if (medico == null) return "redirect:/presentation/login/show";

        medico.setEspecialidad(especialidad);
        medico.setCiudad(ciudad);
        medico.setCostoConsulta(BigDecimal.valueOf(costoConsulta));
        medico.setDuracionCita(duracionCita);
        medico.setHospital(hospital);

        consultorioService.actualizarMedico(medico);

        return "redirect:/profile/medico?success=Perfil actualizado correctamente";
    }

    // ======================== PACIENTE ========================

    @GetMapping("/paciente")
    public String profilePaciente(Model model, HttpSession session) {
        String pacienteId = (String) session.getAttribute("usuarioId");
        if (pacienteId == null) return "redirect:/presentation/login/show";

        Paciente paciente = consultorioService.buscarPacientePorId(pacienteId);
        if (paciente == null) return "redirect:/presentation/login/show";

        model.addAttribute("paciente", paciente);
        return "presentation/profile/profilePaciente";
    }

    @PostMapping("/paciente/update")
    public String updatePaciente(
            @RequestParam("telefono") String telefono,
            @RequestParam("direccion") String direccion,
            HttpSession session) {

        String pacienteId = (String) session.getAttribute("usuarioId");
        if (pacienteId == null) return "redirect:/presentation/login/show";

        Paciente paciente = consultorioService.buscarPacientePorId(pacienteId);
        if (paciente == null) return "redirect:/presentation/login/show";

        paciente.setTelefono(telefono);
        paciente.setDireccion(direccion);

        consultorioService.actualizarPaciente(paciente);

        return "redirect:/profile/paciente?success=Perfil actualizado correctamente";
    }
}
