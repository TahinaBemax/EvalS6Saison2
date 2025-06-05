package itu.mg.rh.controller;

import itu.mg.rh.dto.ApiResponse;
import itu.mg.rh.exception.FrappeApiException;
import itu.mg.rh.models.Departement;
import itu.mg.rh.services.impl.DepartementServiceImpl;
import itu.mg.rh.services.SalarySlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DepartmentController {

    private final DepartementServiceImpl departmentService;
    private final SalarySlipService salarySlipService;

    @Autowired
    DepartmentController(SalarySlipService salarySlipService, DepartementServiceImpl departmentService) {
        this.salarySlipService = salarySlipService;
        this.departmentService = departmentService;
    }


    @GetMapping("/departments")
    @ResponseBody
    public ResponseEntity<?> getAllDepartments() {
        try {
            List<Departement> departments = departmentService.getAllDepartments();
            return ResponseEntity.ok(new ApiResponse<>(departments, "fetched successfully", "success", null));
        } catch (FrappeApiException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getErrorResponse());
        }
    }
}
