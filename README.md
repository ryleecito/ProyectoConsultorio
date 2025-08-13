# 🏥 Proyecto Consultorio

"Proyecto Consultorio" es una aplicación web integral para la gestión de citas médicas. Desarrollada con **Spring Boot 3.4.3** y **Java 21**, la aplicación está diseñada para conectar a médicos, pacientes y administradores en un entorno seguro y eficiente.

---

## 🛠️ Tecnologías Utilizadas

- **Backend:** Spring Boot 3.4.3, Spring Data JPA, Spring Security, Spring Web MVC
- **Lenguaje de programación:** Java 21
- **Motor de vistas:** Thymeleaf
- **Base de datos:** MySQL 8+
- **Validaciones:** Spring Validation
- **Construcción:** Maven

---

## ✨ Características Principales

### **👨‍💻 Gestión de Usuarios y Seguridad**
- **Registro de usuarios:** Pacientes y médicos pueden registrarse, incluyendo la carga de fotos de perfil.
- **Autenticación y roles:** Implementación de **Spring Security** para un control de acceso robusto con roles de usuario (ADMIN, MEDICO, PACIENTE).
- **Aprobación de médicos:** Los administradores pueden revisar, aprobar o rechazar las solicitudes de registro de nuevos médicos.

### **🗓️ Funcionalidades para Pacientes**
- **Búsqueda avanzada:** Los pacientes pueden buscar médicos por especialidad y ciudad.
- **Programación de citas:** Visualización de la disponibilidad de los médicos y agendamiento de citas de forma sencilla.
- **Historial de citas:** Acceso a un registro completo de citas pasadas y futuras.

### **👩‍⚕️ Funcionalidades para Médicos**
- **Gestión de perfil:** Los médicos pueden actualizar su perfil y administrar su información.
- **Disponibilidad y horarios:** Herramienta para definir y gestionar los horarios disponibles para citas.
- **Gestión de citas:** Posibilidad de confirmar, cancelar o marcar citas como atendidas, y agregar observaciones médicas.

---

## 🚀 Requisitos y Configuración

### **Requisitos Previos**
- **JDK 21** o superior.
- **Maven**
- **MySQL 8+**

### **Pasos para la Configuración**

1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/ryleecito/ProyectoConsultorio.git](https://github.com/ryleecito/ProyectoConsultorio.git)
    cd ProyectoConsultorio
    ```

2.  **Configurar la base de datos:**
    - Ajusta la conexión a la base de datos MySQL en el archivo `src/main/resources/application.properties`.
    - **Opcional:** Cambia las credenciales por defecto del usuario administrador (`admin`/`admin`) en el mismo archivo.
    - Crea la base de datos `consultoriodb` e importa el esquema y los datos iniciales ejecutando el script `src/main/resources/Data/consultoriodb.sql`.

3.  **Ejecutar la aplicación:**
    ```bash
    mvn spring-boot:run
    ```

---

¡Listo! La aplicación estará disponible en `http://localhost:8080`.
