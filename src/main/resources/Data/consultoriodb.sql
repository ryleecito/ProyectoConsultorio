-- 1️⃣ ELIMINAR BASE DE DATOS SI EXISTE Y CREARLA
DROP DATABASE IF EXISTS consultoriodb;
CREATE DATABASE IF NOT EXISTS consultoriodb;
USE consultoriodb;

-- 2️⃣ CREACIÓN DE TABLAS

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
    duracion_cita INT DEFAULT 30, -- en minutos
    hospital VARCHAR(100),
    foto VARCHAR(255),
    FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE
    );

-- Tabla de pacientes
CREATE TABLE IF NOT EXISTS pacientes (
                                         id VARCHAR(100) PRIMARY KEY,
    telefono VARCHAR(20) NULL DEFAULT NULL,
    direccion VARCHAR(200) NULL DEFAULT NULL,
    foto VARCHAR(255)
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

-- 3️⃣ INSERCIÓN DE USUARIOS

INSERT INTO usuarios (id, password, nombre, email, rol, estado, fecha_registro) VALUES
-- ADMIN
('admin', '$2a$10$czpVQTEQFFQu09k/TdKaeOi5.x1x8lJNCgrkKwSM8ya/i61sjpLsi', 'Administrador del Sistema', 'admin@consultorio.com', 'ADMIN', 'ACTIVO', NOW()),

-- MEDICOS
('Ana Lopez', '$2a$10$3qaRi/CCNo4IaHJsP6gzVOXjbmUwWsnaixWrQA90JvhDW3tK9Icci', 'Ana Lopez', 'ana.lopez@consultorio.com', 'MEDICO', 'ACTIVO', NOW()),
('Carlos Rodriguez', '$2a$10$3qaRi/CCNo4IaHJsP6gzVOXjbmUwWsnaixWrQA90JvhDW3tK9Icci', 'Carlos Rodriguez', 'carlos.rodriguez@consultorio.com', 'MEDICO', 'ACTIVO', NOW()),
('Carmen Fernandez', '$2a$10$3qaRi/CCNo4IaHJsP6gzVOXjbmUwWsnaixWrQA90JvhDW3tK9Icci', 'Carmen Fernandez', 'carmen.fernandez@consultorio.com', 'MEDICO', 'ACTIVO', NOW()),
('Elena Vargas', '$2a$10$3qaRi/CCNo4IaHJsP6gzVOXjbmUwWsnaixWrQA90JvhDW3tK9Icci', 'Elena Vargas', 'elena.vargas@consultorio.com', 'MEDICO', 'ACTIVO', NOW()),
('Gabriel Torres', '$2a$10$3qaRi/CCNo4IaHJsP6gzVOXjbmUwWsnaixWrQA90JvhDW3tK9Icci', 'Gabriel Torres', 'gabriel.torres@consultorio.com', 'MEDICO', 'ACTIVO', NOW()),
('Juan Sanchez', '$2a$10$3qaRi/CCNo4IaHJsP6gzVOXjbmUwWsnaixWrQA90JvhDW3tK9Icci', 'Juan Sanchez', 'juan.sanchez@consultorio.com', 'MEDICO', 'ACTIVO', NOW()),
('Luis Martinez', '$2a$10$3qaRi/CCNo4IaHJsP6gzVOXjbmUwWsnaixWrQA90JvhDW3tK9Icci', 'Luis Martinez', 'luis.martinez@consultorio.com', 'MEDICO', 'ACTIVO', NOW()),
('Maria Gonzalez', '$2a$10$3qaRi/CCNo4IaHJsP6gzVOXjbmUwWsnaixWrQA90JvhDW3tK9Icci', 'Maria Gonzalez', 'maria.gonzalez@consultorio.com', 'MEDICO', 'ACTIVO', NOW()),
('Roberto Diaz', '$2a$10$3qaRi/CCNo4IaHJsP6gzVOXjbmUwWsnaixWrQA90JvhDW3tK9Icci', 'Roberto Diaz', 'roberto.diaz@consultorio.com', 'MEDICO', 'ACTIVO', NOW()),
('Sofia Ramirez', '$2a$10$3qaRi/CCNo4IaHJsP6gzVOXjbmUwWsnaixWrQA90JvhDW3tK9Icci', 'Sofia Ramirez', 'sofia.ramirez@consultorio.com', 'MEDICO', 'ACTIVO', NOW()),

-- PACIENTES
('paciente1', '$2a$10$S98Elqeig6FXiinqG7naa.eeM7sqxYFl..senX8ROTVY4e2oET.cu', 'Pedro Perez', 'pedro.perez@consultorio.com', 'PACIENTE', 'ACTIVO', NOW());

-- 4️⃣ INSERCIÓN DE MÉDICOS EN LA TABLA "medicos"

INSERT INTO medicos (id, especialidad, ciudad, costo_consulta, duracion_cita, hospital, foto) VALUES
                                                                                                  ('Ana Lopez', 'Pediatria', 'Alajuela', 70.00, 30, 'Hospital San Rafael', 'images/analopez.png'),
                                                                                                  ('Carlos Rodriguez', 'Cardiologia', 'San Jose', 80.00, 45, 'Hospital CIMA', 'images/carlosrodriguez.png'),
                                                                                                  ('Carmen Fernandez', 'Oftalmologia', 'Alajuela', 75.00, 25, 'Hospital San Carlos', 'images/carmenfernandez.png'),
                                                                                                  ('Elena Vargas', 'Medicina Interna', 'San Jose', 70.00, 45, 'Hospital Calderon Guardia', 'images/elenavargas.png'),
                                                                                                  ('Gabriel Torres', 'Psiquiatria', 'Limon', 95.00, 60, 'Hospital Tony Facio', 'images/gabrieltorres.png'),
                                                                                                  ('Juan Sanchez', 'Dermatologia', 'San Jose', 65.00, 30, 'Hospital Clinica Biblica', 'images/juansanchez.png'),
                                                                                                  ('Luis Martinez', 'Traumatologia', 'Cartago', 75.00, 40, 'Hospital Max Peralta', 'images/luismartinez.png'),
                                                                                                  ('Maria Gonzalez', 'Ginecologia', 'Heredia', 85.00, 45, 'Hospital San Vicente de Paul', 'images/mariagonzalez.png'),
                                                                                                  ('Roberto Diaz', 'Neurologia', 'Guanacaste', 90.00, 50, 'Hospital La Anexion', 'images/robertodiaz.png'),
                                                                                                  ('Sofia Ramirez', 'Endocrinologia', 'Puntarenas', 80.00, 40, 'Hospital Monseñor Sanabria', 'images/sofiaramirez.png');

-- 5️⃣ INSERCIÓN DE PACIENTES

INSERT INTO pacientes (id, telefono, direccion, foto) VALUES
    ('paciente1', '50688887777', 'San Jose', 'images/paciente1.png');
