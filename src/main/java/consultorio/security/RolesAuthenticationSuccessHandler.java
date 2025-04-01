package consultorio.security;

import consultorio.data.MedicoRepository;
import consultorio.data.PacientesRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

@Controller
public class RolesAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final MedicoRepository medicoRepository;
    private final PacientesRepository pacientesRepository;

    public RolesAuthenticationSuccessHandler(MedicoRepository medicoRep, PacientesRepository pacientesRep) {
        super();
        this.setAlwaysUseDefaultTargetUrl(false);
        this.medicoRepository = medicoRep;
        this.pacientesRepository = pacientesRep;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ADMIN")) {
            response.sendRedirect("/admin/medicos-pendientes");
            return;
        }
        if (roles.contains("MEDICO")) {
            String usuarioEstado = (String) request.getSession().getAttribute("usuarioEstado");
            String usuarioId = (String) request.getSession().getAttribute("usuarioId");
            if (Objects.equals(usuarioEstado, "ACTIVO")) {
                if (Objects.equals(medicoRepository.findByIdWithSlots(usuarioId).getEmail(), "PREDET")) {
                    response.sendRedirect("/presentation/profile/medico");
                } else {
                    response.sendRedirect("/presentation/pacientes/show");
                }
            } else {
                response.sendRedirect("/presentation/login/show?error=true&errorMessage=El medico debe ser aprobado para acceder");
            }
            return;
        }

        if (response.isCommitted()) return;

        HttpSession session = request.getSession();
        SavedRequest savedRequest = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (savedRequest != null) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        DefaultSavedRequest savedRequest1 = (DefaultSavedRequest) requestCache.getRequest(request, response);
        String loginUrl = "/login";
        if (savedRequest1 != null && !savedRequest1.getRedirectUrl().contains(loginUrl)) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        String usuarioId = (String) request.getSession().getAttribute("usuarioId");
        if (roles.contains("PACIENTE")) {
            if (Objects.equals(pacientesRepository.findById(usuarioId).get().getEmail(), "PREDET")) {
                response.sendRedirect("/presentation/profile/paciente");
            } else {
                response.sendRedirect("/presentation/medicos/list");
            }
        } else {
            response.sendRedirect("/presentation/login/show");
        }
    }
}
