package itu.mg.erpnext.controller;

import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.ApiResponse;
import itu.mg.erpnext.models.MaterialRequest;
import itu.mg.erpnext.services.ItemService;
import itu.mg.erpnext.services.MaterialRequestService;
import itu.mg.erpnext.services.SupplierQuotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/material_requests")
public class RequestMaterialController extends MainController {
    private final ItemService itemService;
    public static final Logger logger = LoggerFactory.getLogger(SupplierQuotationService.class);
    private final MaterialRequestService materialRequestService;

    public RequestMaterialController(SessionManager sessionManager, ItemService itemService, MaterialRequestService materialRequestService) {
        super(sessionManager);
        this.itemService = itemService;
        this.materialRequestService = materialRequestService;
    }

    @GetMapping("/form")
    public String formPage(){
        if (this.getSessionManager().getSessionCookie() == null) return "redirect:/login";
        return "material_request/form";
    }

    @GetMapping()
    public String materialRequestPage(Model model){
        if (this.getSessionManager().getSessionCookie() == null) return "redirect:/login";
        return "material_request/form";
    }

    @PostMapping()
    public ResponseEntity<?> formPage(@RequestBody MaterialRequest materialRequest){
        if (this.getSessionManager().getSessionCookie() == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, "Need Authentication", "auth"));

        if (materialRequestService.save(materialRequest)){
            return ResponseEntity.ok(new ApiResponse<>(null, "Saved successfully", "success"));
        }

        return ResponseEntity.ok(new ApiResponse<>(null, "Internal Server Error", "error"));
    }
}
