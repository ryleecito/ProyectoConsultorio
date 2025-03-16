-- Creación de la base de datos
drop database consultoriodb;
CREATE DATABASE IF NOT EXISTS consultoriodb;
USE consultoriodb;

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
                                        id VARCHAR(20) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    rol ENUM('MEDICO', 'PACIENTE', 'ADMIN') NOT NULL,
    estado ENUM('PENDIENTE', 'ACTIVO', 'INACTIVO') NOT NULL,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
    );

-- Tabla de médicos
CREATE TABLE IF NOT EXISTS medicos (
                                       id VARCHAR(20) PRIMARY KEY,
    especialidad VARCHAR(100),
    ciudad VARCHAR(100),
    costo_consulta DECIMAL(10,2),
    duracion_cita INT NOT NULL DEFAULT 30, -- en minutos
    hospital VARCHAR(100) NOT NULL,
    foto VARCHAR(255),
    FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE
    );

-- Tabla de pacientes
CREATE TABLE IF NOT EXISTS pacientes (
                                         id VARCHAR(20) PRIMARY KEY,
    telefono VARCHAR(20) NULL DEFAULT NULL,
    direccion VARCHAR(200) NULL DEFAULT NULL,
    FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE
    );

-- Tabla de disponibilidad de médicos (slots)
CREATE TABLE slots (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       medico_id VARCHAR(20) NOT NULL,
                       dia INT NOT NULL, -- Representa el día de la semana (1 = Lunes, 7 = Domingo)
                       hora_inicio TIME NOT NULL,
                       hora_fin TIME NOT NULL,
                       FOREIGN KEY (medico_id) REFERENCES medicos(id) ON DELETE CASCADE
);

-- Tabla de citas
CREATE TABLE IF NOT EXISTS citas (
                                     id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                     medico_id VARCHAR(20) NOT NULL,
    paciente_id VARCHAR(20) NOT NULL,
    fecha DATE NOT NULL,
    estado ENUM('PENDIENTE', 'ATENDIDA', 'CANCELADA'),
    notas VARCHAR(100),
    notas_medico VARCHAR(100),
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medico_id) REFERENCES medicos(id) ON DELETE CASCADE,
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE
    );



