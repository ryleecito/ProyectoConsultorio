package consultorio.presentation.medicos;

import consultorio.logic.Cita;
import consultorio.logic.ConsultorioService;
import consultorio.logic.Medico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Controller
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

    @GetMapping("/show")
    public String show() {
        // Redireccionar a la ruta list para que cargue la información actualizada
        return "redirect:/presentation/medicos/list";
    }

    @ModelAttribute("citas")
    public List<Cita> citas() {
        return new ArrayList<>(); // Lista vacía para citas por defecto
    }

    @PostMapping("/search")
    public String search(@ModelAttribute("medicosSearch") Medico medicoSearch, Model model) {
        List<Medico> resultados = service.medicoSearch(medicoSearch.getEspecialidad(), medicoSearch.getCiudad());
        // Si no hay resultados, asignar lista vacía
        if (resultados == null) {
            resultados = new ArrayList<>();
        }

        // Procesar y agregar datos al modelo
        prepararDatosMedicos(resultados, model, null);

        return "presentation/medicos/View";
    }

    @GetMapping("/list")
    public String listMedicos(Model model, @ModelAttribute("medicosSearch") Medico medicoSearch) {
        List<Medico> resultados = service.medicoSearch(medicoSearch.getEspecialidad(), medicoSearch.getCiudad());
        // Si no hay resultados, asignar lista vacía
        if (resultados == null) {
            resultados = new ArrayList<>();
        }

        // Procesar y agregar datos al modelo
        prepararDatosMedicos(resultados, model, null);

        return "presentation/medicos/View";
    }

    /**
     * Método helper para preparar los datos de médicos y sus citas
     * @param medicos Lista de médicos a procesar
     * @param model Modelo para agregar atributos
     * @param semanaParam Parámetro opcional para especificar la semana
     */
    private void prepararDatosMedicos(List<Medico> medicos, Model model, String semanaParam) {
        LocalDate fechaInicio;

        // Si se proporciona un parámetro de fecha, úsalo; de lo contrario, usa la fecha actual
        if (semanaParam != null && !semanaParam.isEmpty()) {
            try {
                fechaInicio = LocalDate.parse(semanaParam);
            } catch (Exception e) {
                fechaInicio = LocalDate.now();
            }
        } else {
            fechaInicio = LocalDate.now();
        }

        // Ajustar a lunes de la semana seleccionada
        LocalDate inicioSemana = fechaInicio.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime fechaInicioTime = inicioSemana.atStartOfDay();

        // Calcular las fechas para navegación entre semanas
        LocalDate semanaAnterior = inicioSemana.minusWeeks(1);
        LocalDate semanaSiguiente = inicioSemana.plusWeeks(1);

        // Formatear la fecha de inicio
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fechaInicioFormateada = inicioSemana.format(formatter);

        // Crear un mapa para almacenar las citas de cada médico
        Map<String, List<List<Cita>>> citasPorMedico = new HashMap<>();

        // Para cada médico, obtener sus citas
        for (Medico medico : medicos) {
            List<List<Cita>> citasDeLaSemana = medico.obtenerCitasDeLaSemana(inicioSemana);
            citasPorMedico.put(medico.getId(), citasDeLaSemana);

        }

        // Agregar datos al modelo
        model.addAttribute("medicoSearch", medicos);
        model.addAttribute("citasPorMedico", citasPorMedico);
        model.addAttribute("fechaInicio", fechaInicioTime);
        model.addAttribute("fechaInicioFormateada", fechaInicioFormateada);
        model.addAttribute("semanaAnterior", semanaAnterior.format(formatter));
        model.addAttribute("semanaSiguiente", semanaSiguiente.format(formatter));
    }
}