package itu.mg.erpnext.controller;

import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.SupplierResponse;
import itu.mg.erpnext.models.SupplierQuotation;
import itu.mg.erpnext.services.RequestForQuotationService;
import itu.mg.erpnext.services.SupplierService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/suppliers")
public class SupplierController extends MainController{
    private final SupplierService supplierService;
    private final RequestForQuotationService RFQService;

    @Autowired
    public SupplierController(SessionManager sessionManager, SupplierService supplierService,RequestForQuotationService RFQService) {
        super(sessionManager);
        this.supplierService = supplierService;
        this.RFQService = RFQService;
    }

    @GetMapping
    public String chooseSupplierPage(Model model){
        if (this.getSessionManager().getSessionCookie() == null){
            return "redirect:/login";
        }

        SupplierResponse supplierResponse = supplierService.getSuppliers();
        model.addAttribute("suppliers", supplierResponse.getData());
        return "fournisseur/choose-fournisseur";
    }

    @PostMapping("memories_supplier")
    public String memoriesSupplier(@RequestParam("supplier") String supplier, HttpSession session, Model model){
        if (this.getSessionManager().getSessionCookie() == null){
            return "redirect:/login";
        }

        if (supplier == null || supplier.trim().isBlank()){
            model.addAttribute("valueError", "Please, chose a supplier!");
            return this.chooseSupplierPage(model);
        }

        session.setAttribute("supplier", supplier);
        return "redirect:/supplier-quotations";
    }

}
