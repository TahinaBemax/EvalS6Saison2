package itu.mg.rh.controller;

import itu.mg.rh.dto.SalarySlipDTO;
import itu.mg.rh.services.SalarySlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final SalarySlipService salarySlipService;

    @Autowired
    EmployeeController(SalarySlipService salarySlipService) {
        this.salarySlipService = salarySlipService;
    }

    @GetMapping()
    public String employeePage(Model model) {
        return "employee/list";
    }

    @GetMapping("/salary-slip")
    public String salarySlipPage(Model model) {
        List<SalarySlipDTO> salarySlips = salarySlipService.getEmployeeSalarySlips("HR-EMP-00002");
        model.addAttribute("salarySlips", salarySlips);

        return "employee/salary";
    }

    @PostMapping("/{id}/salary-slip/toPdf")
    public String salarySlipPage(@PathVariable("id") String id, @RequestParam(required = false) LocalDate date, Model model) {
        return "employee/list";
    }
}