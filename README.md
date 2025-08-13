
# 🏥 Medical Office Management System

"Medical Office Management System" is a comprehensive web application for medical appointment management. Developed with **Spring Boot 3.4.3** and **Java 21**, the application is designed to connect doctors, patients, and administrators in a secure and efficient environment.

---

## 🛠️ Technologies Used

- **Backend:** Spring Boot 3.4.3, Spring Data JPA, Spring Security, Spring Web MVC
- **Programming Language:** Java 21
- **View Engine:** Thymeleaf
- **Database:** MySQL 8+
- **Validation:** Spring Validation
- **Build Tool:** Maven

---

## ✨ Key Features

### **👨‍💻 User Management and Security**
- **User registration:** Patients and doctors can register, including profile photo uploads
- **Authentication and roles:** **Spring Security** implementation for robust access control with user roles (ADMIN, DOCTOR, PATIENT)
- **Doctor approval:** Administrators can review, approve, or reject new doctor registration requests

### **🗓️ Patient Features**
- **Advanced search:** Patients can search for doctors by specialty and city
- **Appointment scheduling:** View doctor availability and easily schedule appointments
- **Appointment history:** Access to complete records of past and future appointments

### **👩‍⚕️ Doctor Features**
- **Profile management:** Doctors can update their profile and manage their information
- **Availability and schedules:** Tools to define and manage available appointment slots
- **Appointment management:** Ability to confirm, cancel, or mark appointments as attended, and add medical observations

---

## 🚀 Requirements and Setup

### **Prerequisites**
- **JDK 21** or higher
- **Maven**
- **MySQL 8+**

### **Setup Steps**

1. **Clone the repository:**
   ```bash
   git clone https://github.com/ryleecito/ProyectoConsultorio.git
   cd ProyectoConsultorio
   ```

2. **Configure the database:**
   - Adjust the MySQL database connection in `src/main/resources/application.properties`
   - **Optional:** Change the default administrator credentials (`admin`/`admin`) in the same file
   - Create the `consultoriodb` database and import the schema and initial data by running the script `src/main/resources/Data/consultoriodb.sql`

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

---

Ready! The application will be available at `http://localhost:8080`.

---

## 📁 Project Structure

```
ProyectoConsultorio/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/consultorio/
│   │   │       ├── config/           # Security and configuration
│   │   │       ├── controller/       # Web controllers
│   │   │       ├── entity/           # JPA entities
│   │   │       ├── repository/       # Data repositories
│   │   │       ├── service/          # Business logic
│   │   │       └── util/             # Utility classes
│   │   └── resources/
│   │       ├── static/               # CSS, JS, images
│   │       ├── templates/            # Thymeleaf templates
│   │       ├── Data/
│   │       │   └── consultoriodb.sql # Database schema
│   │       └── application.properties
│   └── test/                         # Unit and integration tests
├── pom.xml                          # Maven configuration
└── README.md
```

---


### **Security Configuration**

The application implements role-based security with three main roles:

- **ADMIN**: Full system access, doctor approval management
- **DOCTOR**: Profile management, appointment scheduling, patient interaction
- **PATIENT**: Doctor search, appointment booking, history viewing

---

## 👥 User Roles and Permissions

### **Administrator (ADMIN)**
- ✅ Approve/reject doctor registrations
- ✅ Manage system users
- ✅ Access to all system functionality
- ✅ System configuration and monitoring

### **Doctor (MEDICO)**
- ✅ Update profile and specialties
- ✅ Set availability schedules
- ✅ Confirm/cancel appointments
- ✅ Add medical observations
- ✅ View patient appointment history

### **Patient (PACIENTE)**
- ✅ Search doctors by specialty and location
- ✅ Book available appointments
- ✅ View appointment history
- ✅ Update personal profile
- ✅ Cancel upcoming appointments

---

## 🗄️ Database Schema

### **Main Entities**

**Users (`usuarios`)**
- `id`, `username`, `password`, `email`, `role`
- `nombre`, `apellido`, `telefono`, `foto`
- `activo`, `fecha_registro`

**Doctors (`medicos`)**
- `id`, `usuario_id`, `especialidad`, `ciudad`
- `direccion_consultorio`, `aprobado`
- `fecha_aprobacion`

**Patients (`pacientes`)**
- `id`, `usuario_id`, `fecha_nacimiento`
- `direccion`, `ciudad`

**Appointments (`citas`)**
- `id`, `medico_id`, `paciente_id`
- `fecha_cita`, `hora_inicio`, `hora_fin`
- `estado`, `observaciones`

---

## 📊 API Endpoints

### **Authentication Endpoints**
- `GET /login` - Login page
- `POST /login` - Authentication
- `GET /register` - Registration page
- `POST /register` - User registration

### **Patient Endpoints**
- `GET /patient/dashboard` - Patient dashboard
- `GET /patient/search-doctors` - Doctor search
- `POST /patient/book-appointment` - Book appointment
- `GET /patient/appointments` - Appointment history

### **Doctor Endpoints**
- `GET /doctor/dashboard` - Doctor dashboard
- `GET /doctor/profile` - Profile management
- `POST /doctor/availability` - Set availability
- `GET /doctor/appointments` - Appointment management

### **Admin Endpoints**
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/pending-doctors` - Pending approvals
- `POST /admin/approve-doctor` - Approve doctor
- `GET /admin/users` - User management

---




