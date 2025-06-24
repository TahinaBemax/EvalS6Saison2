package itu.mg.rh.controller;

import itu.mg.rh.components.SessionManager;
import itu.mg.rh.dto.ApiResponse;
import itu.mg.rh.exception.FrappeApiException;
import itu.mg.rh.models.Company;
import itu.mg.rh.models.Employee;
import itu.mg.rh.services.CompanyService;
import itu.mg.rh.services.EmployeeService;
import itu.mg.rh.services.SalarySlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employees")
public class EmployeeController extends MainController{
    private final EmployeeService employeeService;
    private final CompanyService companyService;

    @Autowired
    public EmployeeController(SessionManager sessionManager, EmployeeService employeeService, CompanyService companyService) {
        super(sessionManager);
        this.employeeService = employeeService;
        this.companyService = companyService;
    }

    @GetMapping()
    public String employeePage(Model model) {
        if (!this.isAuthentified()) {
            return "redirect:/login";
        }

        List<Company> companies = companyService.findAll();
        model.addAttribute("companies", companies);
        return "employee/list";
    }

    @GetMapping("/filter")
    @ResponseBody
    public ResponseEntity<?> filterEmployees(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String company)
    {
        if (!this.isAuthentified()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You need to login to get access to this resource.");
        }
        try {
            fullName = (fullName !=null && fullName.trim().isEmpty()) ? null : fullName;
            company = (company !=null && company.trim().isEmpty()) ? null : company;

            List<Employee> employee = employeeService.getEmployee(fullName, company);
            return ResponseEntity.ok(new ApiResponse<>(employee, "fetched successfully", "success", null));
        } catch (FrappeApiException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body( e.getErrorResponse());
        }
    }

}