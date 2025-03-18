package consultorio.security;

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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/about", "/presentation/register/show", "/presentation/medicos/list", "/presentation/login/show").permitAll() // ✅ Páginas públicas
                        .requestMatchers("/css/**", "/images/**", "/js/**").permitAll() // ✅ Archivos estáticos
                        .requestMatchers("/admin/**").hasAuthority("ADMIN") // 🔒 Solo Admins pueden acceder
                        .requestMatchers("/medicos/**").hasAnyAuthority("ADMIN", "MEDICO") // 🔒 Médicos y Admins pueden acceder
                        .requestMatchers("/profile/medico").hasAuthority("MEDICO")
                        .requestMatchers("/profile/paciente").hasAuthority("PACIENTE")
                        .requestMatchers("/presentation/medicos/appointments").hasAuthority("MEDICO")
                        .requestMatchers("/admin/medicos-pendientes").hasAuthority("ADMIN")
                        .requestMatchers("/presentation/medicos/list").hasAuthority("PACIENTE")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/presentation/login/show")
                        .loginProcessingUrl("/login")
                        .successHandler((HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
                            // Obtener el rol del usuario autenticado desde la sesión
                            String usuarioRol = (String) request.getSession().getAttribute("usuarioRol");

                            System.out.println("🔹 Rol del usuario autenticado: " + usuarioRol); // Debugging

                            // Verificar si el rol es válido
                            if (usuarioRol == null) {
                                System.out.println("⚠️ No se encontró el rol en la sesión. Redirigiendo a la página de inicio.");
                                response.sendRedirect("/");
                                return;
                            }

                            // Construir la URL de redirección según el rol
                            String redirectUrl;
                            switch (usuarioRol) {
                                case "MEDICO":
                                    redirectUrl = "/presentation/medicos/show";
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

                            System.out.println("➡️ Redirigiendo a: " + redirectUrl); // Debugging
                            response.sendRedirect(response.encodeRedirectURL(redirectUrl)); // 🔹 Usa encodeRedirectURL() para mayor compatibilidad
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
