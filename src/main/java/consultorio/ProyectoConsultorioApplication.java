package consultorio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
public class ProyectoConsultorioApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProyectoConsultorioApplication.class, args);
    }
}
