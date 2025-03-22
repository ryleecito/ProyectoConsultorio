package consultorio.security;

import consultorio.data.UsuariosRepository;
import consultorio.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.util.Objects;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/about", "/presentation/register/show", "/presentation/login/show", "/presentation/register/process", "/presentation/medicos/View","/presentation/medicos/list", "/presentation/about/**").permitAll()
                        .requestMatchers("/css/**", "/images/**", "/js/**").permitAll() // ✅ Archivos estáticos
                        .requestMatchers("/admin/**").hasAuthority("ADMIN") // 🔒 Solo Admins pueden acceder
                        .requestMatchers("/profile/paciente/**").hasAuthority("PACIENTE")
                        .requestMatchers("/admin/medicos-pendientes").hasAuthority("ADMIN")
                        .requestMatchers("/presentation/profile/medico", "presentation/profile/profileMedico").hasAuthority("MEDICO")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/presentation/login/show")
                        .loginProcessingUrl("/login")
                        .failureUrl("/presentation/login/show?error=true")
                        .successHandler((HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
                            // Obtener el rol y estado del usuario desde la sesión
                            String usuarioRol = (String) request.getSession().getAttribute("usuarioRol");
                            String usuarioEstado = (String) request.getSession().getAttribute("usuarioEstado");

                            // Verificar si el rol es válido
                            if (usuarioRol == null) {
                                System.out.println("No se encontró el rol en la sesión. Redirigiendo a la página de inicio.");
                                response.sendRedirect("/");
                                return;
                            }

                            // Construir la URL de redirección según el rol
                            String redirectUrl;
                            switch (usuarioRol) {
                                case "MEDICO":
                                    if (Objects.equals(usuarioEstado, "ACTIVO")) {
                                        redirectUrl = "/presentation/profile/medico";
                                    } else {
                                        redirectUrl = "/presentation/login/show?error=true&errorMessage=El medico debe ser aprobado para acceder";
                                    }
                                    break;
                                case "ADMIN":
                                    redirectUrl = "/admin/medicos-pendientes";
                                    break;
                                case "PACIENTE":
                                    redirectUrl = "/presentation/medicos/list";
                                    break;
                                default:
                                    redirectUrl = "/";
                                    break;
                            }

                            response.sendRedirect(response.encodeRedirectURL(redirectUrl));
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/presentation/login/show?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}