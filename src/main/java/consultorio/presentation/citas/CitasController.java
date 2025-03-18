package consultorio.presentation.citas;

import consultorio.logic.Cita;
import consultorio.logic.ConsultorioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CitasController {

    @Autowired
    private ConsultorioService service;

    @GetMapping("/presentation/medicos/show")
    public String mostrarCitas(Model model) {
        return "/presentation/medicos/appointments";
    }

    @GetMapping("/presentation/citas/show")
    public String mostrarCitas(Model model, HttpSession session) {
        String medicoId = (String) session.getAttribute("usuarioActual"); // Obtener el usuario actual desde la sesión
        if (medicoId != null) {
            List<Cita> citas = service.obtenerCitasPorMedico(medicoId);
            model.addAttribute("citas", citas);
        }
        return "/presentation/medicos/appointments";
    }
}