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
                                       id VARCHAR(100) PRIMARY KEY,
    especialidad VARCHAR(100),
    ciudad VARCHAR(100),
    costo_consulta DECIMAL(10,2),
    duracion_cita INT DEFAULT 30, -- en minutos
    hospital VARCHAR(100),
    foto VARCHAR(255)

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

INSERT INTO usuarios (id, password, nombre, email, rol, estado) VALUES
    ('admin', 'root', 'Administrador del Sistema', 'admin@consultorio.com', 'ADMIN', 'ACTIVO');
ALTER TABLE citas
    DROP COLUMN estado;

ALTER TABLE citas
    ADD estado LONGTEXT NULL;

INSERT INTO usuarios (id, password, nombre, email, rol, estado) VALUES
                                                                    ('Carlos Rodríguez', 'password123', 'Carlos Rodríguez', 'carlos.rodriguez@consultorio.com', 'MEDICO', 'ACTIVO'),
                                                                    ('Ana López', 'password123', 'Ana López', 'ana.lopez@consultorio.com', 'MEDICO', 'ACTIVO'),
                                                                    ('Luis Martínez', 'password123', 'Luis Martínez', 'luis.martinez@consultorio.com', 'MEDICO', 'ACTIVO'),
                                                                    ('María González', 'password123', 'María González', 'maria.gonzalez@consultorio.com', 'MEDICO', 'ACTIVO'),
                                                                    ('Juan Sánchez', 'password123', 'Juan Sánchez', 'juan.sanchez@consultorio.com', 'MEDICO', 'ACTIVO'),
                                                                    ('Carmen Fernández', 'password123', 'Carmen Fernández', 'carmen.fernandez@consultorio.com', 'MEDICO', 'ACTIVO'),
                                                                    ('Roberto Díaz', 'password123', 'Roberto Díaz', 'roberto.diaz@consultorio.com', 'MEDICO', 'ACTIVO'),
                                                                    ('Sofía Ramírez', 'password123', 'Sofía Ramírez', 'sofia.ramirez@consultorio.com', 'MEDICO', 'ACTIVO'),
                                                                    ('Gabriel Torres', 'password123', 'Gabriel Torres', 'gabriel.torres@consultorio.com', 'MEDICO', 'ACTIVO'),
                                                                    ('Elena Vargas', 'password123', 'Elena Vargas', 'elena.vargas@consultorio.com', 'MEDICO', 'ACTIVO');

-- Ahora insertamos la información específica de los médicos
INSERT INTO medicos (id, especialidad, ciudad, costo_consulta, duracion_cita, hospital, foto) VALUES
                                                                                                  ('Carlos Rodríguez', 'Cardiología', 'San José', 80.00, 45, 'Hospital CIMA', '/fotos/rodriguez.jpg'),
                                                                                                  ('Ana López', 'Pediatría', 'Alajuela', 70.00, 30, 'Hospital San Rafael', '/fotos/lopez.jpg'),
                                                                                                  ('Luis Martínez', 'Traumatología', 'Cartago', 75.00, 40, 'Hospital Max Peralta', '/fotos/martinez.jpg'),
                                                                                                  ('María González', 'Ginecología', 'Heredia', 85.00, 45, 'Hospital San Vicente de Paul', '/fotos/gonzalez.jpg'),
                                                                                                  ('Juan Sánchez', 'Dermatología', 'San José', 65.00, 30, 'Hospital Clínica Bíblica', '/fotos/sanchez.jpg'),
                                                                                                  ('Carmen Fernández', 'Oftalmología', 'Alajuela', 75.00, 25, 'Hospital San Carlos', '/fotos/fernandez.jpg'),
                                                                                                  ('Roberto Díaz', 'Neurología', 'Guanacaste', 90.00, 50, 'Hospital La Anexión', '/fotos/diaz.jpg'),
                                                                                                  ('Sofía Ramírez', 'Endocrinología', 'Puntarenas', 80.00, 40, 'Hospital Monseñor Sanabria', '/fotos/ramirez.jpg'),
                                                                                                  ('Gabriel Torres', 'Psiquiatría', 'Limón', 95.00, 60, 'Hospital Tony Facio', '/fotos/torres.jpg'),
                                                                                                  ('Elena Vargas', 'Medicina Interna', 'San José', 70.00, 45, 'Hospital Calderón Guardia', '/fotos/vargas.jpg');
