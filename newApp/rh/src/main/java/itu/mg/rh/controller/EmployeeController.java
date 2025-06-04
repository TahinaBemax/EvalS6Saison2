package itu.mg.rh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import itu.mg.rh.dto.ApiResponse;
import itu.mg.rh.exception.FrappeApiException;
import itu.mg.rh.models.Company;
import itu.mg.rh.models.Departement;
import itu.mg.rh.models.Employee;
import itu.mg.rh.models.SalarySlip;
import itu.mg.rh.services.CompanyService;
import itu.mg.rh.services.DepartementService;
import itu.mg.rh.services.EmployeeService;
import itu.mg.rh.services.SalarySlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final CompanyService companyService;
    private final SalarySlipService salarySlipService;

    @Autowired
    EmployeeController(SalarySlipService salarySlipService, EmployeeService employeeService, CompanyService companyService) {
        this.salarySlipService = salarySlipService;
        this.employeeService = employeeService;
        this.companyService = companyService;
    }

    @GetMapping()
    public String employeePage(Model model) {
        List<Company> companies = companyService.findAll();
        model.addAttribute("companies", companies);
        return "employee/list";
    }

    @GetMapping("/filter")
    @ResponseBody
    public ResponseEntity<?> filterEmployees(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String company) {
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