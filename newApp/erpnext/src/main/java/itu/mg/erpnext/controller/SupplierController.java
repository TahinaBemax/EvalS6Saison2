package itu.mg.erpnext.controller;

import itu.mg.erpnext.dto.RequestForQuotationResponse;
import itu.mg.erpnext.dto.SupplierResponse;
import itu.mg.erpnext.models.RequestForQuotation;
import itu.mg.erpnext.services.RequestForQuotationService;
import itu.mg.erpnext.services.SupplierService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
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
    public SupplierController(SupplierService supplierService,RequestForQuotationService RFQService) {
        this.supplierService = supplierService;
        this.RFQService = RFQService;
    }

    @GetMapping
    public String chooseSupplierPage(Model model){
        SupplierResponse supplierResponse = supplierService.getSuppliers();
        model.addAttribute("suppliers", supplierResponse.getData());
        return "fournisseur/choose-fournisseur";
    }

    @PostMapping("memories_supplier")
    public String memoriesSupplier(@RequestParam("supplier") String supplier, HttpSession session, Model model){
        if (supplier == null || supplier.trim().isBlank()){
            model.addAttribute("valueError", "Please, chose a supplier!");
            return this.chooseSupplierPage(model);
        }

        session.setAttribute("supplier", supplier);
        return String.format("redirect:/suppliers/%s/requests-for-quotation", UriUtils.encodePath(supplier, StandardCharsets.UTF_8));
        //return getSupplierQuoteRequests(supplier, model);
    }

    @GetMapping("/{name}/requests-for-quotation")
    public String getSupplierQuoteRequests(@PathVariable String name, Model model){
        List<RequestForQuotation> supplierQuotations = this.RFQService.getSupplierQuotation(name);
        model.addAttribute("quotations", supplierQuotations);
        return "devis/quotation-request";
    }




}
