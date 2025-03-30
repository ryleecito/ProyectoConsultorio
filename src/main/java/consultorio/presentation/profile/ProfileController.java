package consultorio.presentation.profile;

import consultorio.data.MedicoRepository;
import consultorio.data.SlotsRepository;
import consultorio.logic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
@RequestMapping("/presentation/profile")
public class ProfileController {

    @Autowired
    private ConsultorioService consultorioService;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private SlotsRepository slotsRepository;

    @Value("${picturesPath}")
    private String picturesPath;

    // ======================== MÉDICO ========================

    @GetMapping("/medico")
    public String profileMedico(Model model, HttpSession session) {

        String medicoId = (String) session.getAttribute("usuarioId");
        if (medicoId == null) {
            return "redirect:/presentation/login/show";
        }

        Medico medico = consultorioService.buscarMedicoPorId(medicoId);
        if (medico == null) {
            return "redirect:/presentation/login/show";
        }

        Usuario usuario = consultorioService.buscarPorUsername(medicoId);


        Set<Slot> slots = medico.getSlots();

        model.addAttribute("medico", medico);
        model.addAttribute("usuario", usuario);
        model.addAttribute("slots", slots);

        return "presentation/profile/profileMedico";
    }


    @PostMapping("/medico/update")
    public String updateMedicoProfile(
            @RequestParam("especialidad") String especialidad,
            @RequestParam("ciudad") String ciudad,
            @RequestParam("costo_consulta") BigDecimal costoConsulta,
            @RequestParam("duracion_cita") Integer duracionCita,
            @RequestParam("hospital") String hospital,
            @RequestParam("email") String email,
            @RequestParam("telefono") String telefono,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            HttpSession session) {


        String userId = (String) session.getAttribute("usuarioId");


        if (userId == null) {
            return "redirect:/presentation/login/show";
        }


        Medico medico = medicoRepository.findById(userId).orElse(null);
        if (medico == null) {
            return "redirect:/presentation/login/show";
        }


        medico.setEspecialidad(especialidad);
        medico.setCiudad(ciudad);
        medico.setCostoConsulta(costoConsulta);
        medico.setDuracionCita(duracionCita);
        medico.setHospital(hospital);
        medico.setEmail(email);
        medico.setTelefono(telefono);

        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            try {
                String originalFilename = profilePhoto.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                String fileName = userId + fileExtension;


                String uploadDir = picturesPath;
                Path uploadPath = Paths.get(uploadDir);


                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }


                Path filePath = uploadPath.resolve(fileName);
                Files.copy(profilePhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


                medico.getUsuario().setFoto("/image/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();

            }
        }

        consultorioService.actualizarMedico(medico);


        return "redirect:/presentation/profile/medico?success";
    }

    @PostMapping("/medico/slot")
    public String addMedicoSlot(
            @RequestParam("dia") Integer dia,
            @RequestParam("hora_inicio") String horaInicio,
            @RequestParam("hora_fin") String horaFin,
            HttpSession session) {


        String medicoId = (String) session.getAttribute("usuarioId");
        if (medicoId == null) {
            return "redirect:/presentation/login/show";
        }


        Medico medico = medicoRepository.findById(medicoId).orElse(null);
        if (medico == null) {
            return "redirect:/presentation/login/show";
        }


        Slot slotExistente = null;
        for (Slot s : medico.getSlots()) {
            if (s.getDia().equals(dia)) {
                slotExistente = s;
                break;
            }
        }

        if (slotExistente != null) {

            slotExistente.setHoraInicio(LocalTime.parse(horaInicio));
            slotExistente.setHoraFin(LocalTime.parse(horaFin));
            slotsRepository.save(slotExistente);
        } else {

            Slot nuevoSlot = new Slot();
            nuevoSlot.setMedico(medico);
            nuevoSlot.setDia(dia);
            nuevoSlot.setHoraInicio(LocalTime.parse(horaInicio));
            nuevoSlot.setHoraFin(LocalTime.parse(horaFin));
            slotsRepository.save(nuevoSlot);
        }


        return "redirect:/presentation/profile/medico?slotSuccess";
    }

    @PostMapping("/medico/slot/delete")
    public String deleteMedicoSlot(
            @RequestParam("dia") Integer dia,
            HttpSession session) {


        String medicoId = (String) session.getAttribute("usuarioId");
        if (medicoId == null) {
            return "redirect:/presentation/login/show";
        }


        Medico medico = medicoRepository.findById(medicoId).orElse(null);
        if (medico == null) {
            return "redirect:/presentation/login/show";
        }


        Slot slot = slotsRepository.findByMedicoIdAndDia(medicoId, dia);
        if (slot == null) {
            return "redirect:/presentation/profile/medico?error=SlotNotFound";
        }


        medico.getSlots().remove(slot);

        medicoRepository.save(medico);


        slotsRepository.delete(slot);


        return "redirect:/presentation/profile/medico?slotDeleted";
    }
    // ======================== PACIENTE ========================

    @GetMapping("/paciente")
    public String profilePaciente(Model model, HttpSession session) {
        String pacienteId = (String) session.getAttribute("usuarioId");
        if (pacienteId == null) return "redirect:/presentation/login/show";

        Paciente paciente = consultorioService.buscarPacientePorId(pacienteId);
        if (paciente == null) return "redirect:/presentation/login/show";

        Usuario usuario = consultorioService.buscarPorUsername(pacienteId);

        model.addAttribute("paciente", paciente);
        model.addAttribute("usuario", usuario);
        return "presentation/profile/profilePaciente";
    }

    @PostMapping("/paciente/update")
    public String updatePacienteProfile(
            @RequestParam("telefono") String telefono,
            @RequestParam("direccion") String direccion,
            @RequestParam("email") String email,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            HttpSession session) {


        String userId = (String) session.getAttribute("usuarioId");


        if (userId == null) {
            return "redirect:/presentation/login/show";
        }


        Paciente paciente = consultorioService.buscarPacientePorId(userId);
        if (paciente == null) {
            return "redirect:/presentation/login/show";
        }


        paciente.setTelefono(telefono);
        paciente.setDireccion(direccion);
        paciente.setEmail(email);

        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            try {
                String originalFilename = profilePhoto.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                String fileName = userId + fileExtension;


                String uploadDir = picturesPath;
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }


                Path filePath = uploadPath.resolve(fileName);
                Files.copy(profilePhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


                paciente.getUsuario().setFoto("/image/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();

            }
        }


        consultorioService.actualizarPaciente(paciente);


        return "redirect:/presentation/profile/paciente?success";
    }
}