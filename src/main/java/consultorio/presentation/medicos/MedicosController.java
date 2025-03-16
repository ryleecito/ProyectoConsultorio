package consultorio.presentation.medicos;

import consultorio.data.MedicoRepository;
import consultorio.logic.ConsultorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller("medicos")
@RequestMapping("/presentation/medicos")

public class MedicosController {

    @Autowired
    private ConsultorioService service;

}
