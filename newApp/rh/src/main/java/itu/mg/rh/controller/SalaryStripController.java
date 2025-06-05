package itu.mg.rh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import itu.mg.rh.components.SessionManager;
import itu.mg.rh.dto.ApiResponse;
import itu.mg.rh.exception.FrappeApiException;
import itu.mg.rh.models.SalarySlip;
import itu.mg.rh.services.impl.DepartementServiceImpl;
import itu.mg.rh.services.EmployeeService;
import itu.mg.rh.services.SalarySlipService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/salary-slips")
public class SalaryStripController extends MainController{
    private final SalarySlipService salarySlipService;

    @Autowired
    public SalaryStripController(SessionManager sessionManager, SalarySlipService salarySlipService) {
        super(sessionManager);
        this.salarySlipService = salarySlipService;
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