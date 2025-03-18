package consultorio.presentation.admin;

import consultorio.logic.Usuario;
import consultorio.logic.ConsultorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ConsultorioService medicoService;

    @GetMapping("/medicos-pendientes")
    public String listarMedicosPendientes(Model model) {
        List<Usuario> medicos = medicoService.obtenerMedicosPendientes();
        model.addAttribute("medicos", medicos);
        return "/presentation/admin/medicos-pendientes";
    }

    @PostMapping("/aprobar-medico/{id}")
    public String aprobarMedico(@PathVariable String id, RedirectAttributes redirectAttributes) {
        medicoService.aprobarMedico(id);
        redirectAttributes.addFlashAttribute("mensaje", "Médico aprobado correctamente");
        return "redirect:/admin/medicos-pendientes";
    }


    @PostMapping("/rechazar-medico/{id}")
    public String rechazarMedico(@PathVariable String id, RedirectAttributes redirectAttributes) {
        medicoService.rechazarMedico(id);
        redirectAttributes.addFlashAttribute("mensaje", "Médico rechazado correctamente");
        return "redirect:/admin/medicos-pendientes";
    }
}
