package itu.mg.rh.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import itu.mg.rh.components.SessionManager;
import itu.mg.rh.dto.ApiResponse;
import itu.mg.rh.exception.FrappeApiException;
import itu.mg.rh.models.SalarySlip;
import itu.mg.rh.services.SalarySlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/")
public class DashboardController extends MainController{

    private final SalarySlipService salarySlipService;

    @Autowired
    public DashboardController(SessionManager sessionManager, SalarySlipService salarySlipService) {
        super(sessionManager);
        this.salarySlipService = salarySlipService;
    }

    @GetMapping
    public String dashboard(){
        if (getSessionManager().getSessionCookie() == null) {
            return "redirect:/login";
        }

        return "dashboard/index";
    }

    @GetMapping("salary-slips/details")
    @ResponseBody
    public ResponseEntity<?> getSalarySlipWithSalaryDetais(@RequestParam(name = "month", required = false) Integer month){
        try {
            List<SalarySlip> salarySlipsByMonth = salarySlipService.getSalarySlipsWithSalaryDetail(month);

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
}
