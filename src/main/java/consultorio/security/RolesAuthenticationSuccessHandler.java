package consultorio.security;

import consultorio.data.MedicoRepository;
import consultorio.data.PacientesRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

@Controller
public class RolesAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public MedicoRepository medicoRepository;
    public PacientesRepository pacientesRepository;

    public RolesAuthenticationSuccessHandler(MedicoRepository medicoRep, PacientesRepository pacientesRep) {
        super();
        this.setAlwaysUseDefaultTargetUrl(false);
        medicoRepository = medicoRep;
        pacientesRepository = pacientesRep;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) return;

        HttpSession session = request.getSession(false);
        SavedRequest savedRequest = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");


        boolean usarSavedRequest = false;
        boolean isAppointmentDetailsRequest = false;

        if (savedRequest != null) {
            String redirectUrl = savedRequest.getRedirectUrl();


            if (redirectUrl != null && redirectUrl.contains("/presentation/medicos/appointment-details")) {
                isAppointmentDetailsRequest = true;

            }

            else if (redirectUrl != null &&
                    !redirectUrl.contains("/login") &&
                    !redirectUrl.contains("/css/") &&
                    !redirectUrl.contains("/images/") &&
                    !redirectUrl.contains("/image/") &&
                    !redirectUrl.contains("/js/") &&
                    redirectUrl.contains("/presentation/")) {

                usarSavedRequest = true;
            } else {

                session.removeAttribute("SPRING_SECURITY_SAVED_REQUEST");
                new HttpSessionRequestCache().removeRequest(request, response);
            }
        }


        if (usarSavedRequest && !isAppointmentDetailsRequest) {
            try {
                // Intentar usar la redirección guardada
                super.onAuthenticationSuccess(request, response, authentication);
                return;
            } catch (Exception e) {
                System.out.println("ERROR al usar savedRequest: " + e.getMessage());

                session.removeAttribute("SPRING_SECURITY_SAVED_REQUEST");
                new HttpSessionRequestCache().removeRequest(request, response);
            }
        }


        String usuarioEstado = (String) request.getSession().getAttribute("usuarioEstado");
        String usuarioId = (String) request.getSession().getAttribute("usuarioId");

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains("PACIENTE")) {
            if (Objects.equals(pacientesRepository.findById(usuarioId).get().getEmail(), "PREDET")) {

                if (isAppointmentDetailsRequest) {
                    response.sendRedirect("/presentation/profile/paciente?error=true");
                } else {
                    response.sendRedirect("/presentation/profile/paciente");
                }
            } else {


                if (isAppointmentDetailsRequest) {
                    super.onAuthenticationSuccess(request, response, authentication);
                } else {
                    response.sendRedirect("/presentation/medicos/list");
                }
            }
        } else if (roles.contains("MEDICO")) {
            if (Objects.equals(usuarioEstado, "ACTIVO")) {
                if (Objects.equals(medicoRepository.findByIdWithSlots(usuarioId).getEmail(), "PREDET")) {
                    response.sendRedirect("/presentation/profile/medico");
                } else {
                    response.sendRedirect("/presentation/pacientes/show");
                }
            } else {
                response.sendRedirect("/presentation/login/show?error=true&errorMessage=El medico debe ser aprobado para acceder");
            }
        } else if (roles.contains("ADMIN")) {
            response.sendRedirect("/admin/medicos-pendientes");
        } else {
            response.sendRedirect("/presentation/login/show");
        }
    }
}