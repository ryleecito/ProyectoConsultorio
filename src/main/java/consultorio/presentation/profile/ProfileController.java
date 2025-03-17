package consultorio.presentation.profile;

import consultorio.data.MedicoRepository;
import consultorio.logic.Medico;
import consultorio.logic.ConsultorioService;
import consultorio.logic.Paciente;
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
import java.util.Objects;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ConsultorioService consultorioService;

    @Autowired
    private MedicoRepository medicoRepository;

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

//    @PostMapping("/medico/update")
//    public String updateMedico(
//            @RequestParam("especialidad") String especialidad,
//            @RequestParam("ciudad") String ciudad,
//            @RequestParam("costo_consulta") Double costoConsulta,
//            @RequestParam("duracion_cita") Integer duracionCita,
//            @RequestParam("hospital") String hospital,
//            HttpSession session) {
//
//        String medicoId = (String) session.getAttribute("usuarioId");
//        if (medicoId == null) return "redirect:/presentation/login/show";
//
//        Medico medico = consultorioService.buscarMedicoPorId(medicoId);
//        if (medico == null) return "redirect:/presentation/login/show";
//
//        medico.setEspecialidad(especialidad);
//        medico.setCiudad(ciudad);
//        medico.setCostoConsulta(BigDecimal.valueOf(costoConsulta));
//        medico.setDuracionCita(duracionCita);
//        medico.setHospital(hospital);
//
//        consultorioService.actualizarMedico(medico);
//
//        return "redirect:/profile/medico?success=Perfil actualizado correctamente";
//    }
    @PostMapping("/medico/update")
    public String updateMedicoProfile(
            @RequestParam("especialidad") String especialidad,
            @RequestParam("ciudad") String ciudad,
            @RequestParam("costo_consulta") BigDecimal costoConsulta,
            @RequestParam("duracion_cita") Integer duracionCita,
            @RequestParam("hospital") String hospital,
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

        // Handle file upload
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            try {
                // Generate unique filename
                String fileName = userId + "_" + System.currentTimeMillis() + "_" +
                        Objects.requireNonNull(profilePhoto.getOriginalFilename()).replaceAll("\\s+", "_");

                // Define the path where the file will be saved
                String uploadDir = "src/main/resources/static/images/";
                Path uploadPath = Paths.get(uploadDir);

                // Create directory if it doesn't exist
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Save the file
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(profilePhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Store the relative path in the database
                medico.setFoto("/images/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                // Handle error
            }
        }

        // Save updated medico
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

        model.addAttribute("paciente", paciente);
        return "presentation/profile/profilePaciente";
    }

//    @PostMapping("/paciente/update")
//    public String updatePaciente(
//            @RequestParam("telefono") String telefono,
//            @RequestParam("direccion") String direccion,
//            HttpSession session) {
//
//        String pacienteId = (String) session.getAttribute("usuarioId");
//        if (pacienteId == null) return "redirect:/presentation/login/show";
//
//        Paciente paciente = consultorioService.buscarPacientePorId(pacienteId);
//        if (paciente == null) return "redirect:/presentation/login/show";
//
//        paciente.setTelefono(telefono);
//        paciente.setDireccion(direccion);
//
//        consultorioService.actualizarPaciente(paciente);
//
//        return "redirect:/profile/paciente?success=Perfil actualizado correctamente";
//    }

    @PostMapping("/paciente/update")
    public String updatePacienteProfile(
            @RequestParam("telefono") String telefono,
            @RequestParam("direccion") String direccion,
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

        // Handle file upload
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            try {
                // Generate unique filename
                String fileName = userId + "_" + System.currentTimeMillis() + "_" +
                        Objects.requireNonNull(profilePhoto.getOriginalFilename()).replaceAll("\\s+", "_");

                // Define the path where the file will be saved
                String uploadDir = "src/main/resources/static/images/";
                Path uploadPath = Paths.get(uploadDir);

                // Create directory if it doesn't exist
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Save the file
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(profilePhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Store the relative path in the database
                paciente.setFoto("/images/" + fileName);

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
