package consultorio.presentation.login;

import org.springframework.beans.factory.annotation.Autowired;
import consultorio.logic.ConsultorioService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller("login")
public class Controller {
    @Autowired
    private ConsultorioService service;

    @GetMapping("/presentation/login/show")
    public String show(Model model) {
        return "presentation/login/View";
    }

}