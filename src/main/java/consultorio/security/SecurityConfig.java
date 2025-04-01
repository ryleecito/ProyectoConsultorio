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
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
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
                        .requestMatchers("/", "/about", "/presentation/register/show", "/presentation/login/show","/login", "/presentation/register/process", "/presentation/about/show","/presentation/medicos/list").permitAll()
                        .requestMatchers("/css/**", "/images/**","/image/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/presentation/medicos/appointment-details","/presentation/citas/list", "/presentation/historial/show").hasAuthority("PACIENTE")
                        .requestMatchers("/admin/medicos-pendientes").hasAuthority("ADMIN")
                        .requestMatchers("/presentation/profile/medico", "presentation/citas/list","/presentation/pacientes/show","/presentation/pacientes/atender","/presentation/pacientes/cancelar","/presentation/pacientes/search").hasAuthority("MEDICO")
                        .requestMatchers("/presentation/pacientes/observaciones","/presentation/pacientes/guardarObservaciones").hasAnyAuthority("MEDICO","PACIENTE")
                        .anyRequest().authenticated()
                )
                 .formLogin(customizer -> customizer
                                 .loginPage("/presentation/login/show")
                                 .loginProcessingUrl("/login")
                                 .permitAll()
                                 .failureUrl("/presentation/login/show?error=Contrasena o usuario incorrecto")
                                 .successHandler(new RolesAuthenticationSuccessHandler(medicoRepository,pacientesRepository))
                )
                .logout(logout -> logout
                        .logoutUrl("/presentation/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/presentation/logout", "GET"))
                        .logoutSuccessUrl("/presentation/login/show?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(customizer -> customizer
                    .accessDeniedPage("/presentation/login/access-denied")
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}