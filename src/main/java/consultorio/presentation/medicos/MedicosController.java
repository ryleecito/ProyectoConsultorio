package consultorio.presentation.medicos;

import consultorio.logic.Cita;
import consultorio.logic.ConsultorioService;
import consultorio.logic.Medico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

        // Obtener weekOffset del modelo o usar 0 por defecto
        Integer weekOffset = (Integer) model.getAttribute("weekOffset");
        if (weekOffset == null) {
            weekOffset = 0;
        }

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

        // Aplicar el weekOffset a la fecha
        fechaInicio = fechaInicio.plusWeeks(weekOffset);

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

            // Verificar y actualizar atributos básicos de citas existentes
            for (List<Cita> citasDelDia : citasDeLaSemana) {
                for (Cita cita : citasDelDia) {


                        Cita citaExistente = service.findCitaByFechaAndMedicoId(cita.getFecha(), medico.getId());

                        if (citaExistente != null) {
                            cita.setEstado("Pendiente");
                        }

                }
            }

            citasPorMedico.put(medico.getId(), citasDeLaSemana);
        }

        // Agregar datos al modelo
        model.addAttribute("medicoSearch", medicos);
        model.addAttribute("citasPorMedico", citasPorMedico);
        model.addAttribute("fechaInicio", fechaInicioTime);
        model.addAttribute("fechaInicioFormateada", fechaInicioFormateada);
        model.addAttribute("semanaAnterior", semanaAnterior.format(formatter));
        model.addAttribute("semanaSiguiente", semanaSiguiente.format(formatter));

        // Asegurar que weekOffset esté en el modelo para la navegación
        model.addAttribute("weekOffset", weekOffset);
    }

    @GetMapping("/schedule/{medicoId}")
    public String showSchedule(@PathVariable String medicoId,
                               @RequestParam(required = false) String semana,
                               @RequestParam(required = false, defaultValue = "0") Integer weekOffset,
                               Model model) {
        // Obtener el médico por ID
        Medico medico = service.buscarMedicoPorId(medicoId);

        if (medico == null) {
            // Manejar caso cuando no se encuentra el médico
            return "redirect:/presentation/medicos/list";
        }

        // Agregar weekOffset al modelo antes de procesar los datos
        model.addAttribute("weekOffset", weekOffset);

        // Crear una lista con solo este médico
        List<Medico> medicosList = new ArrayList<>();
        medicosList.add(medico);

        // Usar el método helper existente para preparar los datos del médico y sus citas
        prepararDatosMedicos(medicosList, model, semana);

        // Agregar el médico seleccionado al modelo para usarlo en la vista
        model.addAttribute("medicoSeleccionado", medico);
        // También añadir como "medico" para evitar errores
        model.addAttribute("medico", medico);

        return "presentation/medicos/schedule";
    }

    @GetMapping("/appointment-details")
    public String showAppointmentDetails(
            @RequestParam("medicoId") String medicoId,
            @RequestParam("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime fecha,
            Model model) {

        // Buscar el médico por ID
        Medico medico = service.buscarMedicoPorId(medicoId);

        // Buscar la cita por fecha y médico
        Cita cita = service.findCitaByFechaAndMedicoId(fecha, medicoId);

        // Si la cita no existe, crear una nueva
        if (cita == null) {
            cita = new Cita();
            cita.setMedico(medico);
            cita.setFecha(fecha);
            cita.setHoraInicio(fecha.toLocalTime());
            cita.setHoraFin(fecha.toLocalTime().plusMinutes(medico.getDuracionCita()));
            cita.setFechaCreacion(java.time.Instant.now());
            cita.setEstado("Disponible");
        }

        // Se ha eliminado la verificación y actualización del estado a "Defasado"

        // Añadir al modelo para mostrar en la vista
        model.addAttribute("medico", medico);
        model.addAttribute("cita", cita);

        return "presentation/book/confirmar_cita";
    }
}