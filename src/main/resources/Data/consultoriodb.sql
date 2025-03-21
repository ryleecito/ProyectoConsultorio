-- Creación de la base de datos
DROP DATABASE IF EXISTS consultoriodb;
CREATE DATABASE IF NOT EXISTS consultoriodb;
USE consultoriodb;

-- Tabla de usuarios (solo para autenticación y control de acceso)
CREATE TABLE IF NOT EXISTS usuarios (
                                        id VARCHAR(20) PRIMARY KEY,
                                        password VARCHAR(100) NOT NULL,
                                        nombre VARCHAR(100) NOT NULL,
                                        rol ENUM('MEDICO', 'PACIENTE', 'ADMIN') NOT NULL,
                                        estado ENUM('PENDIENTE', 'ACTIVO', 'INACTIVO') NOT NULL,
                                        foto varchar(255),
                                        fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de médicos
CREATE TABLE IF NOT EXISTS medicos (
                                       id VARCHAR(20) PRIMARY KEY,
                                       email VARCHAR(100)  NOT NULL,
                                       especialidad VARCHAR(100),
                                       ciudad VARCHAR(100),
                                       costo_consulta DECIMAL(10,2),
                                       duracion_cita INT DEFAULT 30, -- en minutos
                                       hospital VARCHAR(100),
                                       telefono VARCHAR(20),
                                       FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla de pacientes
CREATE TABLE IF NOT EXISTS pacientes (
                                         id VARCHAR(20) PRIMARY KEY,
                                         email VARCHAR(100) NOT NULL,
                                         telefono VARCHAR(20),
                                         direccion VARCHAR(200),
                                         fecha_nacimiento DATE,
                                         FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla de administradores
CREATE TABLE IF NOT EXISTS administradores (
                                               id VARCHAR(20) PRIMARY KEY,
                                               email VARCHAR(100) NOT NULL,
                                               telefono VARCHAR(20),
                                               departamento VARCHAR(100),
                                               FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla de disponibilidad de médicos (slots)
CREATE TABLE slots (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       medico_id VARCHAR(20) NOT NULL,
                       dia INT NOT NULL,
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
                                     hora_inicio TIME NOT NULL,
                                     hora_fin TIME NOT NULL,
                                     estado ENUM('PENDIENTE', 'ATENDIDA', 'CANCELADA'),
                                     notas_paciente VARCHAR(500),
                                     notas_medico VARCHAR(500),
                                     fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (medico_id) REFERENCES medicos(id) ON DELETE CASCADE,
                                     FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE
);

-- Insertar usuario admin
INSERT INTO usuarios (id, password,nombre, rol, estado,foto) VALUES
    ('admin', '$2a$10$czpVQTEQFFQu09k/TdKaeOi5.x1x8lJNCgrkKwSM8ya/i61sjpLsi','Administrador del Sistema', 'ADMIN', 'ACTIVO',null);

-- Insertar detalles del admin
INSERT INTO administradores (id, email, telefono, departamento) VALUES
    ('admin', 'admin@consultorio.com', '123456789', 'TI');
