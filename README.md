
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

## 🔧 Configuration

### **Database Configuration**

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/consultoriodb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Admin Default Credentials (Change in production)
app.admin.username=admin
app.admin.password=admin
```

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

## 🔒 Security Features

### **Authentication**
- **Spring Security** with custom login and registration
- **Password encryption** using BCrypt
- **Session management** with timeout configuration
- **CSRF protection** enabled

### **Authorization**
- **Role-based access control** (RBAC)
- **Method-level security** for sensitive operations
- **URL-based protection** for different user types
- **File upload security** with type and size validation

### **Data Protection**
- **Input validation** using Bean Validation
- **SQL injection prevention** through JPA
- **XSS protection** via Thymeleaf escaping
- **Secure file handling** for profile photos

---

## 🧪 Testing

### **Run Tests**

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserControllerTest

# Run integration tests
mvn test -Dgroups=integration
```

### **Test Coverage**

The application includes:
- **Unit tests** for service layer
- **Integration tests** for repositories
- **Web layer tests** for controllers
- **Security tests** for authentication

---

## 🚀 Deployment

### **Production Configuration**

1. **Environment Variables:**
   ```bash
   export SPRING_PROFILES_ACTIVE=production
   export DB_HOST=your-production-db-host
   export DB_USERNAME=your-db-username
   export DB_PASSWORD=your-db-password
   ```

2. **Build for Production:**
   ```bash
   mvn clean package -Pprod
   ```

3. **Run with Production Profile:**
   ```bash
   java -jar target/consultorio-1.0.0.jar --spring.profiles.active=production
   ```

### **Docker Deployment**

```dockerfile
FROM openjdk:21-jre-slim
COPY target/consultorio-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# Build and run
docker build -t consultorio-app .
docker run -p 8080:8080 consultorio-app
```

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

## 🐛 Troubleshooting

### **Common Issues**

**Database Connection Error:**
```bash
# Check MySQL service
sudo systemctl status mysql

# Verify database exists
mysql -u root -p -e "SHOW DATABASES;"

# Check connection properties
grep -n "spring.datasource" src/main/resources/application.properties
```

**Port Already in Use:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change port in application.properties
server.port=8081
```

**File Upload Issues:**
```bash
# Check file permissions
ls -la src/main/resources/static/uploads/

# Create upload directory
mkdir -p src/main/resources/static/uploads/
chmod 755 src/main/resources/static/uploads/
```

---

## 🔄 Development Workflow

### **Adding New Features**

1. **Create feature branch:**
   ```bash
   git checkout -b feature/appointment-reminders
   ```

2. **Add entity/repository/service/controller**
3. **Add corresponding tests**
4. **Update documentation**
5. **Submit pull request**

### **Code Style**
- Follow **Google Java Style Guide**
- Use **meaningful variable names**
- Add **JavaDoc** for public methods
- Maintain **high test coverage**

---

## 📈 Performance Optimization

### **Database Optimization**
- **Connection pooling** with HikariCP
- **Query optimization** with JPA Criteria API
- **Database indexing** on frequently queried fields
- **Lazy loading** for entity relationships

### **Caching Strategy**
- **Spring Cache** for frequently accessed data
- **Session caching** for user authentication
- **Static resource caching** with appropriate headers

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### **Development Guidelines**
- Write tests for new features
- Follow existing code style
- Update documentation
- Add meaningful commit messages

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Authors

- **Ryleecito** - *Initial work* - [@ryleecito](https://github.com/ryleecito)

---

## 📞 Support

- **Issues:** [GitHub Issues](https://github.com/ryleecito/ProyectoConsultorio/issues)
- **Documentation:** [Wiki](https://github.com/ryleecito/ProyectoConsultorio/wiki)
- **Email:** support@consultorio-project.com

---

**Made with ❤️ for healthcare professionals** 🏥✨
