package itu.mg.rh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import itu.mg.rh.components.SessionManager;
import itu.mg.rh.dto.AdvancedUpdateSalaryAssignmentDTO;
import itu.mg.rh.dto.SalaryDTO;
import itu.mg.rh.exception.FrappeApiException;
import itu.mg.rh.models.Employee;
import itu.mg.rh.models.SalaryComponent;
import itu.mg.rh.services.EmployeeService;
import itu.mg.rh.services.SalaryComponentService;
import itu.mg.rh.services.SalaryStructureAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/salary-structure-assignment")
public class SalaryStuctureAssignmentController extends MainController{
    private final SalaryStructureAssignmentService salaryService;
    private final EmployeeService employeeService;
    private final SalaryComponentService salaryComponentService;

    @Autowired
    public SalaryStuctureAssignmentController(SessionManager sessionManager,
                                              SalaryStructureAssignmentService salaryService,
                                              EmployeeService employeeService, SalaryComponentService salaryComponentService) {
        super(sessionManager);
        this.salaryService = salaryService;
        this.employeeService = employeeService;
        this.salaryComponentService = salaryComponentService;
    }


    @PostMapping("/save")
    public String save(SalaryDTO salaryDTO, RedirectAttributes redirectAttributes){
        try {
            boolean isSaved = this.salaryService.saveAll(salaryDTO);

            if (isSaved){
                redirectAttributes.addFlashAttribute("success", "Saved");
            } else {
                redirectAttributes.addFlashAttribute("error", "Internal Server Error");
            }
        } catch (FrappeApiException e) {
            redirectAttributes.addFlashAttribute("error", e.getErrorResponse().serverMessageToString());
        }

        return "redirect:/salary-structure-assignment";
    }
    @GetMapping()
    public String salarySlipPage(Model model) throws JsonProcessingException {
        if (!this.isAuthentified()) {
            return "redirect:/";
        }

        List<Employee> employees = employeeService.findAll();
        model.addAttribute("employees", employees);

        return "salaryAssignment/create";
    }

    @GetMapping("/advanced-update")
    public String advancedUpdatePage(Model model) throws JsonProcessingException {
        if (!this.isAuthentified()) {
            return "redirect:/";
        }

        List<SalaryComponent> components = salaryComponentService.findAll();
        model.addAttribute("components", components);
        model.addAttribute("salaryAssignmentDTO", new AdvancedUpdateSalaryAssignmentDTO());

        return "salaryAssignment/advanced-update-form";
    }

    @PostMapping("/advanced-update")
    public String saveUpdate(@RequestAttribute(name = "salaryAssignmentDTO")
                                 AdvancedUpdateSalaryAssignmentDTO data,
                             RedirectAttributes redirectAttributes) throws JsonProcessingException
    {
        if (!this.isAuthentified()) {
            return "redirect:/";
        }

        return "salaryAssignment/advanced-update-form";
    }

}