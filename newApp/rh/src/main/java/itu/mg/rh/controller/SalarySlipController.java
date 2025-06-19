package itu.mg.rh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import itu.mg.rh.components.SessionManager;
import itu.mg.rh.dto.ApiResponse;
import itu.mg.rh.dto.SalaryDTO;
import itu.mg.rh.dto.SalarySlipUpdateDTO;
import itu.mg.rh.exception.FrappeApiException;
import itu.mg.rh.models.SalaryComponent;
import itu.mg.rh.models.SalarySlip;
import itu.mg.rh.services.SalaryComponentService;
import itu.mg.rh.services.SalarySlipService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/salary-slips")
public class SalarySlipController extends MainController{
    private final SalarySlipService salarySlipService;
    private final SalaryComponentService salaryComponentService;

    @Autowired
    public SalarySlipController(SessionManager sessionManager, SalarySlipService salarySlipService, SalaryComponentService salaryComponentService) {
        super(sessionManager);
        this.salarySlipService = salarySlipService;
        this.salaryComponentService = salaryComponentService;
    }

    @GetMapping("/details")
    @ResponseBody
    public ResponseEntity<?> getSalarySlipWithSalaryDetais(
            @RequestParam(name = "month", required = false) Integer month)
    {
        if (!this.isAuthentified()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You need to login to get access to this resource.");
        }

        try {
            List<SalarySlip> salarySlipsByMonth = salarySlipService.findAllWithDetails(month, null);

            return ResponseEntity.ok(salarySlipsByMonth);
        } catch (FrappeApiException e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, "An error occured when fetching salary Slip", "error", e.getErrorResponse().getServerMessages()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, "Internal Server error", "error", List.of(e.getLocalizedMessage())));
        }
    }

    @GetMapping("/update")
    public String advancedUpdatePage(Model model, RedirectAttributes redirectAttributes){
        if (!this.isAuthentified())
            return "redirect:/login";

        List<SalaryComponent> salaryComponents = salaryComponentService.findAll();
        model.addAttribute("salarySlip", new SalarySlipUpdateDTO());
        model.addAttribute("salaryComponents", salaryComponents );
        return "salarySlip/update";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute(name = "salarySlip") SalarySlipUpdateDTO salarySlip,
                         RedirectAttributes redirectAttributes,
                         BindingResult bindingResult, Model model)
    {
        if (!this.isAuthentified())
            return "redirect:/login";

        if(bindingResult.hasErrors()){
            return advancedUpdatePage(model, redirectAttributes);
        }

        try {
            if (salarySlipService.batchUpdate(salarySlip)){
                redirectAttributes.addFlashAttribute("success", "Updated");
                return "redirect:/salary-slips";
            }
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof FrappeApiException ex){
                model.addAttribute("error", ex.getMessages());
            } else {
                model.addAttribute("error", e.getMessage());
            }
        }

        return advancedUpdatePage(model, redirectAttributes);
    }
    @GetMapping()
    public String salarySlipPage(Model model) throws JsonProcessingException {
        if (!this.isAuthentified()) {
            return "redirect:/";
        }
        List<SalarySlip> salarySlips = salarySlipService.getSalarySlips();
        model.addAttribute("salarySlips", salarySlips);

        return "employee/salary";
    }


    @PostMapping("/toPdf")
    @ResponseBody
    public ResponseEntity<?> salarySlipToPdf(@RequestParam(value = "id", required = true) String id, HttpServletResponse response) throws IOException
    {
        if (!this.isAuthentified()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You need to login to get access to this resource.");
        }

        if (id == null || id.trim().isBlank()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(null,
                            "SalarySlipName is required!",
                            "error",
                            List.of("SalarySlipName is null"))
                    );
        }

        try {
            byte[] bytes = salarySlipService.exportSalarySlipToPdf(id);
            if (bytes != null) {
                String fileName = String.format("salary-slip-%s-%s", id, LocalDateTime.now());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", fileName);
                return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
            }

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null,
                            "Failed to generate PDF",
                            "error",
                            null)
                    );
        } catch (FrappeApiException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(null,
                            e.getMessage(),
                            "error",
                            e.getErrorResponse().getServerMessages())
                    );
        }
    }

    private void backup() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "filename");
        //return new ResponseEntity<>(bytes, headers, HttpStatus.OK);

    }
}