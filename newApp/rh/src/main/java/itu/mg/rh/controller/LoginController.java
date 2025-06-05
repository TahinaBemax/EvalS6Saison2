package itu.mg.rh.controller;

import itu.mg.rh.models.login.LoginRequest;
import itu.mg.rh.services.impl.LoginServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController{
    private final LoginServiceImpl loginServiceImpl;

    public LoginController(LoginServiceImpl loginServiceImpl) {
        this.loginServiceImpl = loginServiceImpl;
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

            if(loginServiceImpl.login(request.getUsr(), request.getPwd())){
                return "redirect:/";
            }
        } catch (UsernameNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "login/login";
        }

        return "login/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        loginServiceImpl.getSessionManager().setSessionCookie(null);
        return "redirect:/login";
    }
}
