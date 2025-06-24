package itu.mg.rh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import itu.mg.rh.components.SessionManager;
import itu.mg.rh.dto.AdvancedUpdateSalaryAssignmentDTO;
import itu.mg.rh.dto.ApiResponse;
import itu.mg.rh.dto.SalaryDTO;
import itu.mg.rh.dto.SalaryStructureFormDto;
import itu.mg.rh.exception.FrappeApiException;
import itu.mg.rh.models.Employee;
import itu.mg.rh.models.SalaryComponent;
import itu.mg.rh.models.SalaryStructure;
import itu.mg.rh.services.EmployeeService;
import itu.mg.rh.services.SalaryComponentService;
import itu.mg.rh.services.SalaryStructureAssignmentService;
import itu.mg.rh.services.SalaryStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/salary-structures")
public class SalaryStuctureController extends MainController{
    private final SalaryStructureService salaryService;
    private final SalaryComponentService salaryComponentService;

    @Autowired
    public SalaryStuctureController(SessionManager sessionManager,
                                    SalaryStructureService salaryService,
                                    SalaryComponentService salaryComponentService)
    {
        super(sessionManager);
        this.salaryService = salaryService;
        this.salaryComponentService = salaryComponentService;
    }


    @PostMapping("/save")
    public ResponseEntity<?> save(@Validated @RequestBody
                                      SalaryStructureFormDto salaryDTO, BindingResult bindingResult)
    {
        try {
            if (bindingResult.hasErrors()){
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(List.of(salaryDTO), "Bad Request", "error", null));
            }
            boolean isSaved = this.salaryService.save(salaryDTO);

            if (isSaved){
                return ResponseEntity.ok(new ApiResponse<>(null, "Saved", "success", null));
            } else {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(null, "Internal Server Error", "error", null));
            }
        } catch (FrappeApiException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, "Internal Server Error", "error", List.of(e.getMessages())));
        }
    }
    @GetMapping()
    public String salarySlipPage(Model model) throws JsonProcessingException {
        if (!this.isAuthentified()) {
            return "redirect:/";
        }

        List<SalaryStructure> allActiveSalaryStructure = salaryService.findAllActiveSalaryStructure();
        model.addAttribute("salaryStructures", allActiveSalaryStructure);

        return "salaryStructure/list";
    }

    @GetMapping("/form")
    public String formPage(Model model) throws JsonProcessingException {
        if (!this.isAuthentified()) {
            return "redirect:/";
        }

        return "salaryStructure/form";
    }
}