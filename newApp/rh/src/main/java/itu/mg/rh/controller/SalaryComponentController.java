package itu.mg.rh.controller;


import itu.mg.rh.components.SessionManager;
import itu.mg.rh.dto.ApiResponse;
import itu.mg.rh.exception.FrappeApiException;
import itu.mg.rh.models.SalaryComponent;
import itu.mg.rh.services.SalaryComponentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/salary-components")
@Slf4j
public class SalaryComponentController extends MainController{
    final private SalaryComponentService salaryComponentService;

    public SalaryComponentController(SessionManager sessionManager, SalaryComponentService salaryComponentService) {
        super(sessionManager);
        this.salaryComponentService = salaryComponentService;
    }

    @GetMapping()
    public String listSalaryComponents(Model model){
        List<SalaryComponent> all = salaryComponentService.findAll();
        model.addAttribute("salaryComponents", all);
        return "salaryComponent/list";
    }

    @GetMapping("/form")
    public String formSalaryComponent(@RequestAttribute(name = "id", required = false) String id, Model model){
        SalaryComponent component = id != null ? salaryComponentService.findById(id) : new SalaryComponent();
        model.addAttribute("component", component);
        return "salaryComponent/form";
    }

    @PostMapping
    public String save(@Validated @ModelAttribute(name = "component") SalaryComponent component,
                       RedirectAttributes redirectAttributes, BindingResult bindingResult, Model model)
    {
        if (!this.isAuthentified()){
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()){
            return "salaryComponent/form";
        }

        try {
            boolean success = salaryComponentService.save(component);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "Saved");
            } else {
                model.addAttribute("error", "Internal Server Error");
                return formSalaryComponent(null, model);
            }
        } catch (RuntimeException e) {
            if (e instanceof FrappeApiException ex){
                model.addAttribute("error", ex.getMessages());
            } else {
                model.addAttribute("error", "Internal Server Error");
            }
            return formSalaryComponent(null, model);
        }

        return "redirect:/salary-components";
    }
}
