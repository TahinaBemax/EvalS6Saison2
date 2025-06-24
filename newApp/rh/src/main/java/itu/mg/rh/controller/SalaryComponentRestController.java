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

@RestController
@RequestMapping("/api/salary-components")
@Slf4j
public class SalaryComponentRestController extends MainController{
    final private SalaryComponentService salaryComponentService;

    public SalaryComponentRestController(SessionManager sessionManager, SalaryComponentService salaryComponentService) {
        super(sessionManager);
        this.salaryComponentService = salaryComponentService;
    }

    @GetMapping()
    public ResponseEntity<?> apiSalaryComponents(){
        List<SalaryComponent> all = null;
        if (!isAuthentified()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, "Need authentication", "error", null));
        }
        try {
            all = salaryComponentService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(all, "", "success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, "Internal Server Error", "error", List.of(e.getMessage())));
        }
    }
}
