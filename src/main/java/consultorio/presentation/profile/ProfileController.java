package consultorio.presentation.profile;

import consultorio.data.MedicoRepository;
import consultorio.data.SlotsRepository;
import consultorio.logic.*;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ConsultorioService consultorioService;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private SlotsRepository slotsRepository;

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

        Optional<Slot> slots = consultorioService.obtenerSlotsDeMedico(medicoId);


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
            @RequestParam("dia") String dia,
            @RequestParam("hora_inicio") String hora_inicio,
            @RequestParam("hora_fin") String hora_fin,
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
                // Generate unique filename
                String fileName = userId + "_" + System.currentTimeMillis() + "_" +
                        Objects.requireNonNull(profilePhoto.getOriginalFilename()).replaceAll("\\s+", "_");

                // Define the path where the file will be saved
                String uploadDir = "C:\\Users\\Saul Francis\\Desktop\\images_consultorio";
                Path uploadPath = Paths.get(uploadDir);

                // Create directory if it doesn't exist
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Save the file
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(profilePhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Store the relative path in the database
                medico.getUsuario().setFoto("/images_consultorio/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                // Handle error
            }
        }
        Slot slot = new Slot();

        slot.setDia(Integer.parseInt(dia));
        slot.setHoraInicio(LocalTime.parse(hora_inicio));
        slot.setHoraFin(LocalTime.parse(hora_fin));
        slot.setMedico(medico);
        // Save updated medico
        slotsRepository.save(slot);
        medicoRepository.save(medico);

        // Use the same redirect as in your original commented-out method
        return "redirect:/profile/medico?success";
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
                // Generate unique filename
                String fileName = userId + "_" + System.currentTimeMillis() + "_" +
                        Objects.requireNonNull(profilePhoto.getOriginalFilename()).replaceAll("\\s+", "_");

                // Define the path where the file will be saved
                String uploadDir = "C:\\Users\\Saul Francis\\Desktop\\images_consultorio";
                Path uploadPath = Paths.get(uploadDir);

                // Create directory if it doesn't exist
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Save the file
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(profilePhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Store the relative path in the database
                paciente.getUsuario().setFoto("/images_consultorio/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                // Handle error
            }
        }

        // Save updated paciente
        consultorioService.actualizarPaciente(paciente);

        // Redirect to profile page with success message
        return "redirect:/profile/paciente?success";
    }
}
