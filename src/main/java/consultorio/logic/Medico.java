package consultorio.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "medicos")
public class Medico {
    @Id
    @Size(max = 20)
    @Column(name = "id", nullable = false, length = 20)
    private String id;


    @Size(max = 100)
    @NotNull
    @Column(name = "especialidad", nullable = false, length = 100)
    private String especialidad;

    @Size(max = 100)
    @NotNull
    @Column(name = "ciudad", nullable = false, length = 100)
    private String ciudad;

    @NotNull
    @Column(name = "costo_consulta", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoConsulta;

    @NotNull
    @ColumnDefault("30")
    @Column(name = "duracion_cita", nullable = false)
    private Integer duracionCita;

    @Size(max = 100)
    @NotNull
    @Column(name = "hospital", nullable = false, length = 100)
    private String hospital;

    @Size(max = 255)
    @Column(name = "foto")
    private String foto;

    @OneToMany(mappedBy = "medico")
    private Set<Cita> citas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "medico")
    private Set<Slot> slots = new LinkedHashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public BigDecimal getCostoConsulta() {
        return costoConsulta;
    }

    public void setCostoConsulta(BigDecimal costoConsulta) {
        this.costoConsulta = costoConsulta;
    }

    public Integer getDuracionCita() {
        return duracionCita;
    }

    public void setDuracionCita(Integer duracionCita) {
        this.duracionCita = duracionCita;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Set<Cita> getCitas() {
        return citas;
    }

    public void setCitas(Set<Cita> citas) {
        this.citas = citas;
    }

    public Set<Slot> getSlots() {
        return slots;
    }

    public void setSlots(Set<Slot> slots) {
        this.slots = slots;
    }

    public List<List<Cita>> obtenerCitasDeLaSemana(LocalDate start) {
        List<List<Cita>> resultados = new ArrayList<>(); // Lista que contendrá las citas de cada día

        // Ajustar al lunes de la semana actual
        LocalDate inicioSemana = start.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        for (int i = 0; i < 7; i++) {
            LocalDate fecha = inicioSemana.plusDays(i); // Avanzamos día por día
            List<Cita> citasDelDia = citasDeFecha(fecha); // Llamamos al método para obtener las citas de ese día

            // Siempre agregamos la lista, incluso si está vacía
            resultados.add(citasDelDia);
        }
        return resultados; // Retornamos todos los días, con o sin citas
    }
    public List<Cita> citasDeFecha(LocalDate fecha) {
        List<Cita> citasDisponibles = new ArrayList<>();
        int diaSemana = fecha.getDayOfWeek().getValue(); // 1-Lunes, 7-Domingo

        for (Slot slot : this.slots) {
            if (slot.getDia() == diaSemana) {
                LocalTime horaActual = slot.getHoraInicio();

                // Continuar generando citas mientras no excedamos la hora de fin del slot
                while (!horaActual.plus(java.time.Duration.ofMinutes(this.duracionCita)).isAfter(slot.getHoraFin())) {
                    // Crear una nueva cita vacía
                    Cita cita = new Cita();
                    cita.setMedico(this);
                    cita.setPaciente(null); // Sin paciente asignado aún
                    // Convertir LocalDate a LocalDateTime y agregar la hora de inicio
                    cita.setFecha(fecha.atTime(horaActual));  // Aquí combinamos la fecha con la hora

                    cita.setFechaCreacion(java.time.Instant.now());
                    cita.setEstado("Disponible");

                    // Agregar la cita a la lista
                    citasDisponibles.add(cita);

                    // Avanzar a la siguiente hora de cita basada en la duración configurada
                    horaActual = horaActual.plusMinutes(this.duracionCita);
                }
            }
        }
        return citasDisponibles;
    }
}