package itu.mg.erpnext.controller;

import itu.mg.erpnext.models.login.LoginRequest;
import itu.mg.erpnext.services.LoginService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController extends MainController{
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String loginPages(){
        return "login/login";
    }
    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("LoginRequest") LoginRequest request,
                        BindingResult bindingResult,
                        Model model) {
        try {
            if (bindingResult.hasErrors()){
                return "login/login";
            }

            if(loginService.login(request.getUsr(), request.getPwd())){
                return "redirect:/suppliers";
            }
        } catch (UsernameNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "login/login";
        }

        return "login/login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }
}
