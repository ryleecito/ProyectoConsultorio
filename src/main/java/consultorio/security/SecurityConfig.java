package consultorio.security;

import consultorio.data.MedicoRepository;
import consultorio.data.PacientesRepository;
import consultorio.data.UsuariosRepository;
import consultorio.logic.ConsultorioService;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Objects;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    public MedicoRepository medicoRepository;

    @Autowired
    public PacientesRepository pacientesRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/about", "/presentation/register/show", "/presentation/login/show", "/presentation/register/process", "/presentation/medicos/**","/presentation/medicos/list", "/presentation/about/**").permitAll()
                        .requestMatchers("/css/**", "/images/**", "/js/**","/image/**").permitAll() // ✅ Archivos estáticos
                        .requestMatchers("/admin/**").hasAuthority("ADMIN") //  
                        .requestMatchers("/profile/paciente/**").hasAuthority("PACIENTE")
                        .requestMatchers("/admin/medicos-pendientes").hasAuthority("ADMIN")
                        .requestMatchers("/presentation/profile/medico", "presentation/profile/profileMedico", "presentation/citas/list").hasAuthority("MEDICO")
                        .requestMatchers("/presentation/citas/list").hasAuthority("PACIENTE")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/presentation/login/show")
                        .loginProcessingUrl("/login")
                        .failureUrl("/presentation/login/show?error=true&errorMessage=Perfil no existente")
                        .successHandler((HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {

                            String usuarioRol = (String) request.getSession().getAttribute("usuarioRol");
                            String usuarioEstado = (String) request.getSession().getAttribute("usuarioEstado");
                            String usuarioId = (String) request.getSession().getAttribute("usuarioId");


                            if (usuarioRol == null) {
                                System.out.println("No se encontró el rol en la sesión. Redirigiendo a la página de inicio.");
                                response.sendRedirect("/");
                                return;
                            }

                            String redirectUrl;
                            switch (usuarioRol) {
                                case "MEDICO":

                                    if (Objects.equals(usuarioEstado, "ACTIVO")) {
                                        if(Objects.equals(medicoRepository.findByIdWithSlots(usuarioId).getEmail(), "PREDET")) {
                                            redirectUrl = "/presentation/profile/medico";
                                        }
                                        else
                                        {
                                            redirectUrl = "/presentation/pacientes/show";
                                        }

                                    } else {
                                        redirectUrl = "/presentation/login/show?error=true&errorMessage=El medico debe ser aprobado para acceder";
                                    }
                                    break;
                                case "ADMIN":
                                    redirectUrl = "/admin/medicos-pendientes";
                                    break;
                                case "PACIENTE":
                                    if(Objects.equals(pacientesRepository.findById(usuarioId).get().getEmail(), "PREDET")) {
                                        redirectUrl = "/presentation/profile/paciente";
                                    }
                                    else
                                    {
                                        redirectUrl = "/presentation/medicos/list";
                                    }

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
                        .logoutUrl("/presentation/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/presentation/logout", "GET"))
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