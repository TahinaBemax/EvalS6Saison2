package itu.mg.rh.controller;


import itu.mg.rh.components.SessionManager;
import itu.mg.rh.models.SalaryComponent;
import itu.mg.rh.services.SalaryComponentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
