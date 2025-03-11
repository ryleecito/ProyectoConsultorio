package consultorio.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "medicos")
public class Medico {
    @Id
    @Size(max = 20)
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidade especialidad;

    @NotNull
    @Column(name = "costo_consulta", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoConsulta;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "localidad_id", nullable = false)
    private Localidade localidad;

    @NotNull
    @Column(name = "duracion_cita", nullable = false)
    private Integer duracionCita;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @Size(max = 255)
    @Column(name = "foto")
    private String foto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Especialidade getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidade especialidad) {
        this.especialidad = especialidad;
    }

    public BigDecimal getCostoConsulta() {
        return costoConsulta;
    }

    public void setCostoConsulta(BigDecimal costoConsulta) {
        this.costoConsulta = costoConsulta;
    }

    public Localidade getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidade localidad) {
        this.localidad = localidad;
    }

    public Integer getDuracionCita() {
        return duracionCita;
    }

    public void setDuracionCita(Integer duracionCita) {
        this.duracionCita = duracionCita;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

}