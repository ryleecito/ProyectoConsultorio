package consultorio.security;


import consultorio.data.MedicoRepository;
import consultorio.data.PacientesRepository;
import consultorio.logic.ConsultorioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

@Controller
public class RolesAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
implements AuthenticationSuccessHandler {


    public MedicoRepository medicoRepository;

    public PacientesRepository pacientesRepository;

    public RolesAuthenticationSuccessHandler(MedicoRepository medicoRep, PacientesRepository pacientesRep) {
        super();
        this.setAlwaysUseDefaultTargetUrl(false);
        medicoRepository = medicoRep;
        pacientesRepository = pacientesRep;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (response.isCommitted()) return;
        HttpSession session = request.getSession();
        SavedRequest savedRequest = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (savedRequest != null) {
            try {
                super.onAuthenticationSuccess(request, response, authentication);
                return;
            } catch (ServletException e) {
                System.out.println("ERROR: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        String usuarioEstado = (String) request.getSession().getAttribute("usuarioEstado");
        String usuarioId = (String) request.getSession().getAttribute("usuarioId");


        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains("PACIENTE")){
            if(Objects.equals(pacientesRepository.findById(usuarioId).get().getEmail(), "PREDET")) {
                response.sendRedirect("/presentation/profile/paciente");
                }
            else
            {
                response.sendRedirect("/presentation/medicos/list");
            }
        }
        else if (roles.contains("MEDICO")){
            if (Objects.equals(usuarioEstado, "ACTIVO")) {
                if(Objects.equals(medicoRepository.findByIdWithSlots(usuarioId).getEmail(), "PREDET")) {
                    response.sendRedirect("/presentation/profile/medico");
                }
                else
                {
                    response.sendRedirect("/presentation/pacientes/show");
                }
            }
            else {
                response.sendRedirect("/presentation/login/show?error=true&errorMessage=El medico debe ser aprobado para acceder");
            }
        }
        else if (roles.contains("ADMIN")){
            response.sendRedirect("/admin/medicos-pendientes");
        }
        else response.sendRedirect("/presentation/login/show");
    }
}
