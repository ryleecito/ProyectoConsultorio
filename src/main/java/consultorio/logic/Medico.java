package consultorio.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "El ID no puede estar vacío")
    @Size(max = 20, message = "El ID no puede tener más de 20 caracteres")
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @NotBlank(message = "La especialidad no puede estar vacía")
    @Size(max = 100, message = "La especialidad no puede tener más de 100 caracteres")
    @NotNull(message = "La especialidad es obligatoria")
    @Column(name = "especialidad", nullable = false, length = 100)
    private String especialidad;

    @NotBlank(message = "La ciudad no puede estar vacía")
    @Size(max = 100, message = "La ciudad no puede tener más de 100 caracteres")
    @NotNull(message = "La ciudad es obligatoria")
    @Column(name = "ciudad", nullable = false, length = 100)
    private String ciudad;

    @DecimalMin(value = "0.01", message = "El costo de consulta debe ser mayor a 0")
    @NotNull(message = "El costo de consulta es obligatorio")
    @Column(name = "costo_consulta", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoConsulta;

    @NotNull(message = "La duración de la cita es obligatoria")
    @Min(value = 1, message = "La duración de la cita debe ser mayor a 0")
    @ColumnDefault("30")
    @Column(name = "duracion_cita", nullable = false)
    private Integer duracionCita;

    @NotBlank(message = "El hospital no puede estar vacío")
    @Size(max = 100, message = "El hospital no puede tener más de 100 caracteres")
    @NotNull(message = "El hospital es obligatorio")
    @Column(name = "hospital", nullable = false, length = 100)
    private String hospital;

    @OneToMany(mappedBy = "medico")
    private Set<Cita> citas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "medico", fetch = FetchType.EAGER)
    private Set<Slot> slots = new LinkedHashSet<>();

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Usuario usuario;

    @NotBlank(message = "El email no puede estar vacío")
    @Size(max = 100, message = "El email no puede tener más de 100 caracteres")
    @NotNull(message = "El email es obligatorio")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Size(max = 20, message = "El teléfono no puede tener más de 20 caracteres")
    @Column(name = "telefono", length = 20)
    private String telefono;

    @NotBlank(message = "La presentacion no puede estar vacia")
    @Size(max = 100, message = "La presentacion no puede tener mas de 100 caracteres")
    @Column(name = "presentacion", length = 20)
    private String presentacion;


    @Version
    private Long version;


    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuarios) {
        this.usuario = usuarios;
    }

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
        List<List<Cita>> resultados = new ArrayList<>();

        LocalDate inicioSemana = start.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        for (int i = 0; i < 7; i++) {
            LocalDate fecha = inicioSemana.plusDays(i);
            List<Cita> citasDelDia = citasDeFecha(fecha);

            resultados.add(citasDelDia);
        }
        return resultados;
    }

    public List<Cita> citasDeFecha(LocalDate fecha) {
        List<Cita> citasDisponibles = new ArrayList<>();
        int diaSemana = fecha.getDayOfWeek().getValue();
        LocalDateTime ahora = LocalDateTime.now();
        java.time.Instant now = java.time.Instant.now();

        for (Slot slot : this.slots) {
            if (slot.getDia() == diaSemana) {

                long duracionSlotMinutos = java.time.Duration.between(slot.getHoraInicio(), slot.getHoraFin()).toMinutes();
                int maxCitasPosibles = (int) (duracionSlotMinutos / this.duracionCita);


                final int MAX_CITAS_SEGURIDAD = 100;
                int maxCitas = Math.min(maxCitasPosibles, MAX_CITAS_SEGURIDAD)+1;

                LocalTime horaActual = slot.getHoraInicio();
                int contadorCitas = 0;

                while (!horaActual.plus(java.time.Duration.ofMinutes(this.duracionCita)).isAfter(slot.getHoraFin())
                        && contadorCitas < maxCitas) {


                    Cita cita = new Cita();
                    cita.setMedico(this);
                    cita.setPaciente(null);
                    cita.setHoraInicio(horaActual);

                    LocalTime horaFin = horaActual.plusMinutes(this.duracionCita);
                    cita.setHoraFin(horaFin);

                    LocalDateTime fechaHoraInicio = fecha.atTime(horaActual);
                    cita.setFecha(fechaHoraInicio);
                    cita.setFechaCreacion(now);

                    if (fechaHoraInicio.isBefore(ahora)) {
                        cita.setEstado("Pendiente");
                    } else {
                        cita.setEstado("Disponible");
                    }

                    citasDisponibles.add(cita);
                    horaActual = horaActual.plusMinutes(this.duracionCita);
                    contadorCitas++;
                }
            }
        }
        return citasDisponibles;
    }

}