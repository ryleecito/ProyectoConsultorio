package consultorio.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @NotBlank(message = "El ID no puede estar vacío")
    @Size(max = 20, message = "El ID no puede tener más de 20 caracteres")
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El rol no puede estar vacío")
    @Lob
    @Column(name = "rol", nullable = false)
    private String rol;

    @NotBlank(message = "El estado no puede estar vacío")
    @Lob
    @Column(name = "estado", nullable = false)
    private String estado;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha_registro")
    private Instant fechaRegistro;

    @NotBlank(message = "La foto no puede ser nula")
    @Column(name = "foto", nullable = false, length = 100)
    private String foto;

    @OneToOne
    private consultorio.logic.Administrador administrador;

    @OneToOne(mappedBy = "usuario")
    private Medico medico;

    @OneToOne(mappedBy = "usuario")
    private Paciente paciente;


    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public consultorio.logic.Administrador getAdministradore() {
        return administrador;
    }

    public void setAdministradore(consultorio.logic.Administrador administrador) {
        this.administrador = administrador;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Instant getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Instant fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }


    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}