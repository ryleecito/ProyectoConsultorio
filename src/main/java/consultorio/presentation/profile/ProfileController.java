package consultorio.presentation.profile;

import consultorio.data.MedicoRepository;
import consultorio.data.SlotsRepository;
import consultorio.logic.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    @ModelAttribute("slot")
    public Slot getSlot() {
        return new Slot();
    }

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

        if ("PREDET".equalsIgnoreCase(medico.getEmail())) medico.setEmail("");
        if ("PREDET".equalsIgnoreCase(medico.getHospital())) medico.setHospital("");
        if ("PREDET".equalsIgnoreCase(medico.getTelefono())) medico.setTelefono("");
        if ("PREDET".equalsIgnoreCase(medico.getPresentacion())) medico.setPresentacion("");

        Usuario usuario = consultorioService.buscarPorUsername(medicoId);
        Set<Slot> slots = medico.getSlots();

        model.addAttribute("medico", medico);
        model.addAttribute("usuario", usuario);
        model.addAttribute("slots", slots);
        model.addAttribute("slot", new Slot());

        return "presentation/profile/profileMedico";
    }

    @PostMapping("/medico/update")
    public String updateMedicoProfile(
            @Valid @ModelAttribute("medico") Medico medico,
            BindingResult result,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            Model model,
            HttpSession session) {

        String userId = (String) session.getAttribute("usuarioId");
        if (userId == null) {
            return "redirect:/presentation/login/show";
        }


        if (result.hasErrors()) {
            Usuario usuario = consultorioService.buscarPorUsername(userId);
            Set<Slot> slots = medico.getSlots();
            model.addAttribute("usuario", usuario);
            model.addAttribute("slots", slots);
            return "presentation/profile/profileMedico";
        }


        if (consultorioService.emailExists(medico.getEmail(), userId) || consultorioService.telefonoExists(medico.getTelefono(), userId)) {

            if(consultorioService.emailExists(medico.getEmail(), userId)) {
                result.rejectValue("email", "error.email", "Ya hay un usuario con este email");
            }

            if(consultorioService.telefonoExists(medico.getTelefono(), userId)) {
                result.rejectValue("telefono", "error.telefono", "Ya hay un usuario con este telefono");
            }

            Usuario usuario = consultorioService.buscarPorUsername(userId);
            Set<Slot> slots = medico.getSlots();
            model.addAttribute("usuario", usuario);
            model.addAttribute("slots", slots);
            return "presentation/profile/profileMedico";
        }




        Medico actual = medicoRepository.findById(userId).orElse(null);
        if (actual == null) {
            return "redirect:/presentation/login/show";
        }


        actual.setEspecialidad(medico.getEspecialidad());
        actual.setCiudad(medico.getCiudad());
        actual.setCostoConsulta(medico.getCostoConsulta());
        actual.setDuracionCita(medico.getDuracionCita());
        actual.setHospital(medico.getHospital());
        actual.setEmail(medico.getEmail());
        actual.setTelefono(medico.getTelefono());
        actual.setPresentacion(medico.getPresentacion());


        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            try {
                String originalFilename = profilePhoto.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String fileName = userId + fileExtension;
                Path uploadPath = Paths.get(picturesPath);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(profilePhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                actual.getUsuario().setFoto("/image/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        consultorioService.actualizarMedico(actual);
        return "redirect:/presentation/profile/medico?success";
    }



    @PostMapping("/medico/slot")
    public String addMedicoSlot(
            @Valid @ModelAttribute("slot") Slot slot,
            BindingResult result,
            HttpSession session,
            Model model) {

        String medicoId = (String) session.getAttribute("usuarioId");
        if (medicoId == null) {
            return "redirect:/presentation/login/show";
        }

        Medico medico = medicoRepository.findById(medicoId).orElse(null);
        if (medico == null) {
            return "redirect:/presentation/login/show";
        }

        slot.setMedico(medico);


        if (slot.getHoraInicio() != null && slot.getHoraFin() != null &&
                !slot.getHoraInicio().isBefore(slot.getHoraFin())) {
            result.rejectValue("horaFin", "error.horaFin", "La hora de fin debe ser posterior a la de inicio");
        }


        System.out.println("DEBUG >>> Slot recibido:");
        System.out.println("Día: " + slot.getDia());
        System.out.println("Hora inicio: " + slot.getHoraInicio());
        System.out.println("Hora fin: " + slot.getHoraFin());
        System.out.println("¿Tiene errores de validación? " + result.hasErrors());

        result.getFieldErrors().forEach(error -> {
            System.out.println("ERROR EN CAMPO: " + error.getField());
            System.out.println("MENSAJE: " + error.getDefaultMessage());
        });


        if (result.hasErrors()) {
            Usuario usuario = consultorioService.buscarPorUsername(medicoId);
            Set<Slot> slots = medico.getSlots();


            // Limpiar valores predeterminados
            if ("PREDET".equalsIgnoreCase(medico.getEmail())) medico.setEmail("");
            if ("PREDET".equalsIgnoreCase(medico.getHospital())) medico.setHospital("");
            if ("PREDET".equalsIgnoreCase(medico.getTelefono())) medico.setTelefono("");


            model.addAttribute("medico", medico);
            model.addAttribute("usuario", usuario);
            model.addAttribute("slots", slots);
            model.addAttribute("slot", slot);
            return "presentation/profile/profileMedico";
        }


        Slot existente = slotsRepository.findByMedicoIdAndDia(medicoId, slot.getDia());
        if (existente != null) {
            existente.setHoraInicio(slot.getHoraInicio());
            existente.setHoraFin(slot.getHoraFin());
            slotsRepository.save(existente);
        } else {
            slotsRepository.save(slot);
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

        // Limpiar valores predeterminados
        if ("PREDET".equalsIgnoreCase(paciente.getEmail())) paciente.setEmail("");
        if ("PREDET".equalsIgnoreCase(paciente.getTelefono())) paciente.setTelefono("");
        if ("PREDET".equalsIgnoreCase(paciente.getDireccion())) paciente.setDireccion("");

        Usuario usuario = consultorioService.buscarPorUsername(pacienteId);

        model.addAttribute("paciente", paciente);
        model.addAttribute("usuario", usuario);
        return "presentation/profile/profilePaciente";
    }


    @PostMapping("/paciente/update")
    public String updatePacienteProfile(
            @Valid @ModelAttribute("paciente") Paciente paciente,
            BindingResult result,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            Model model,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String userId = (String) session.getAttribute("usuarioId");
        if (userId == null) {
            return "redirect:/presentation/login/show";
        }

        if (result.hasErrors()) {
            Usuario usuario = consultorioService.buscarPorUsername(userId);
            model.addAttribute("usuario", usuario);
            return "presentation/profile/profilePaciente";
        }


        if (consultorioService.emailExists(paciente.getEmail(), userId) || consultorioService.telefonoExists(paciente.getTelefono(), userId)) {

            if(consultorioService.emailExists(paciente.getEmail(), userId)) {
                result.rejectValue("email", "error.email", "Ya hay un usuario con este email");
            }

            if(consultorioService.telefonoExists(paciente.getTelefono(), userId)) {
                result.rejectValue("telefono", "error.telefono", "Ya hay un usuario con este telefono");
            }

            Usuario usuario = consultorioService.buscarPorUsername(userId);
            model.addAttribute("usuario", usuario);
            return "presentation/profile/profilePaciente";
        }




        Paciente actual = consultorioService.buscarPacientePorId(userId);
        if (actual == null) {
            return "redirect:/presentation/login/show";
        }

        actual.setTelefono(paciente.getTelefono());
        actual.setDireccion(paciente.getDireccion());
        actual.setEmail(paciente.getEmail());

        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            try {
                String originalFilename = profilePhoto.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String fileName = userId + fileExtension;
                Path uploadPath = Paths.get(picturesPath);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(profilePhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                actual.getUsuario().setFoto("/image/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        consultorioService.actualizarPaciente(actual);


        String pendingMedicoId = (String) session.getAttribute("PENDING_APPOINTMENT_MEDICO_ID");
        LocalDateTime pendingFecha = (LocalDateTime) session.getAttribute("PENDING_APPOINTMENT_FECHA");

        if (pendingMedicoId != null && pendingFecha != null) {

            session.removeAttribute("PENDING_APPOINTMENT_MEDICO_ID");
            session.removeAttribute("PENDING_APPOINTMENT_FECHA");
            session.removeAttribute("PENDING_APPOINTMENT_URL");


            return "redirect:/presentation/medicos/appointment-details?medicoId=" + pendingMedicoId
                    + "&fecha=" + pendingFecha.format(DateTimeFormatter.ISO_DATE_TIME);
        }

        SavedRequest savedRequest = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (savedRequest != null && savedRequest.getRedirectUrl() != null &&
                savedRequest.getRedirectUrl().contains("/presentation/medicos/appointment-details")) {


            session.removeAttribute("SPRING_SECURITY_SAVED_REQUEST");
            new HttpSessionRequestCache().removeRequest(request, response);

            return "redirect:" + savedRequest.getRedirectUrl();
        }

        return "redirect:/presentation/profile/paciente?success";
    }

}