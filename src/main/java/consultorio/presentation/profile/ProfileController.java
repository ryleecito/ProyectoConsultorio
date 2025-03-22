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

        // Obtenemos los slots directamente del médico
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

        // Get current user ID from session - using the correct attribute name
        String userId = (String) session.getAttribute("usuarioId");

        // Check if user ID is null
        if (userId == null) {
            return "redirect:/presentation/login/show";
        }

        // Get current medico
        Medico medico = medicoRepository.findById(userId).orElse(null);
        if (medico == null) {
            return "redirect:/presentation/login/show";
        }

        // Update medico data
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


                String uploadDir = picturesPath;  // This will be C:/AAA/images/
                Path uploadPath = Paths.get(uploadDir);

                // Create directory if it doesn't exist
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Save the file
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(profilePhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Store the relative path in the database
                medico.getUsuario().setFoto("/image/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                // Handle error
            }
        }
        // Save updated paciente
        consultorioService.actualizarMedico(medico);

        // Use the same redirect as in your original commented-out method
        return "redirect:/presentation/profile/medico?success";
    }

    @PostMapping("/medico/slot")
    public String addMedicoSlot(
            @RequestParam("dia") Integer dia,
            @RequestParam("hora_inicio") String horaInicio,
            @RequestParam("hora_fin") String horaFin,
            HttpSession session) {

        // Obtener el ID del médico de la sesión
        String medicoId = (String) session.getAttribute("usuarioId");
        if (medicoId == null) {
            return "redirect:/presentation/login/show";
        }

        // Buscar el médico por ID
        Medico medico = medicoRepository.findById(medicoId).orElse(null);
        if (medico == null) {
            return "redirect:/presentation/login/show";
        }

        // Verificar si ya existe un slot para el día seleccionado
        Slot slotExistente = null;
        for (Slot s : medico.getSlots()) {
            if (s.getDia().equals(dia)) {
                slotExistente = s;
                break;
            }
        }

        if (slotExistente != null) {
            // Actualizar el slot existente
            slotExistente.setHoraInicio(LocalTime.parse(horaInicio));
            slotExistente.setHoraFin(LocalTime.parse(horaFin));
            slotsRepository.save(slotExistente);
        } else {
            // Crear un nuevo slot si no existe uno para ese día
            Slot nuevoSlot = new Slot();
            nuevoSlot.setMedico(medico);
            nuevoSlot.setDia(dia);
            nuevoSlot.setHoraInicio(LocalTime.parse(horaInicio));
            nuevoSlot.setHoraFin(LocalTime.parse(horaFin));
            slotsRepository.save(nuevoSlot);
        }

        // Redirigir a la página de perfil con mensaje de éxito
        return "redirect:/presentation/profile/medico?slotSuccess";
    }

    @PostMapping("/medico/slot/delete")
    public String deleteMedicoSlot(
            @RequestParam("dia") Integer dia,
            HttpSession session) {

        // Get the médico ID from the session
        String medicoId = (String) session.getAttribute("usuarioId");
        if (medicoId == null) {
            return "redirect:/presentation/login/show";
        }

        // Find the médico
        Medico medico = medicoRepository.findById(medicoId).orElse(null);
        if (medico == null) {
            return "redirect:/presentation/login/show";
        }

        // Find the slot by médico ID and día
        Slot slot = slotsRepository.findByMedicoIdAndDia(medicoId, dia);
        if (slot == null) {
            return "redirect:/presentation/profile/medico?error=SlotNotFound";
        }

        // Remove the slot from the médico's collection
        medico.getSlots().remove(slot);

        // Update the médico
        medicoRepository.save(medico);

        // Delete the slot
        slotsRepository.delete(slot);

        // Redirect back to the profile page with success message
        return "redirect:/presentation/profile/medico?slotDeleted";
    }
    // ======================== PACIENTE ========================

    @GetMapping("/paciente")
    public String profilePaciente(Model model, HttpSession session) {
        String pacienteId = (String) session.getAttribute("usuarioId");
        if (pacienteId == null) return "redirect:/presentation/login/show";

        Paciente paciente = consultorioService.buscarPacientePorId(pacienteId);
        if (paciente == null) return "redirect:/presentation/login/show";

        // Recuperar el objeto Usuario (que contiene la foto)
        Usuario usuario = consultorioService.buscarPorUsername(pacienteId); // O el mét odo que uses para obtenerlo

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

        // Get current user ID from session
        String userId = (String) session.getAttribute("usuarioId");

        // Check if user ID is null
        if (userId == null) {
            return "redirect:/presentation/login/show";
        }

        // Get current paciente
        Paciente paciente = consultorioService.buscarPacientePorId(userId);
        if (paciente == null) {
            return "redirect:/presentation/login/show";
        }

        // Update paciente data
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


                String uploadDir = picturesPath;  // This will be C:/AAA/images/
                Path uploadPath = Paths.get(uploadDir);

                // Create directory if it doesn't exist
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Save the file
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(profilePhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Store the relative path in the database
                paciente.getUsuario().setFoto("/image/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                // Handle error
            }
        }

        // Save updated paciente
        consultorioService.actualizarPaciente(paciente);

        // Redirect to profile page with success message
        return "redirect:/presentation/profile/paciente?success";
    }
}