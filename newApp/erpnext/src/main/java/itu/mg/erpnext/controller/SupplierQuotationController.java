package itu.mg.erpnext.controller;

import itu.mg.erpnext.models.RequestForQuotation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller()
@RequestMapping("/supplier-quotations")
public class SupplierQuotationController {
    @GetMapping("/{id}/update-price")
    public String getSupplierQuoteRequests(@PathVariable String id, @RequestParam(name = "pastPrice") double pastPrice, Model model){
        model.addAttribute("id", id);
        model.addAttribute("pastPrice", pastPrice);
        return "devis/update-price";
    }

    @PostMapping("/{id}/update-price")
    public String updatePrice(@PathVariable String id, @RequestParam(name = "pastPrice") double pastPrice, RedirectAttributes redirectAttributes){

        return "devis/update-price";
    }
}
