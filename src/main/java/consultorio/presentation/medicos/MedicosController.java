package consultorio.presentation.medicos;

import consultorio.data.MedicoRepository;
import consultorio.logic.ConsultorioService;
import consultorio.logic.Medico;
import org.hibernate.validator.constraints.Mod10Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Controller("medicos")
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

    @PostMapping("/search")
    public String search(
            @ModelAttribute("medicosSearch") Medico medicoSearch, Model model) {
        List<Medico> resultados = service.medicoSearch(medicoSearch.getEspecialidad(), medicoSearch.getCiudad());
        // Si resultados es null, asignar una lista vacía
        if (resultados == null) {
            resultados = new ArrayList<>();
        }
        model.addAttribute("medicoSearch", resultados);
        return "presentation/medicos/View";
    }

    @GetMapping("/list")
    public String listMedicos(Model model, @ModelAttribute("medicosSearch") Medico medicoSearch) {
        List<Medico> resultados = service.medicoSearch(medicoSearch.getEspecialidad(), medicoSearch.getCiudad());
        // Si resultados es null, asignar una lista vacía
        if (resultados == null) {
            resultados = new ArrayList<>();
        }
        model.addAttribute("medicoSearch", resultados);
        return "presentation/medicos/View";
    }

}
