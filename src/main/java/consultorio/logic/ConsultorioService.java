package consultorio.logic;

import consultorio.data.Paciente;
import consultorio.data.PacientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
@org.springframework.stereotype.Service("ConsultorioService")

public class ConsultorioService {
    @Autowired
    private PacientesRepository pacientesRepository;

    public Iterable<Paciente> pacientesFindAll() {
        return pacientesRepository.findAll();
    }
}
