package itu.mg.erpnext.controller;

import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.UpdateSupQuotaItemPriceFormData;
import itu.mg.erpnext.exceptions.ActionNotAllowedExcpetion;
import itu.mg.erpnext.exceptions.AmountInvalidExcpetion;
import itu.mg.erpnext.models.SupplierQuotation;
import itu.mg.erpnext.models.SupplierQuotationItem;
import itu.mg.erpnext.services.RequestForQuotationService;
import itu.mg.erpnext.services.SupplierQuotationService;
import itu.mg.erpnext.services.SupplierService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller()
@RequestMapping("/supplier-quotations")
public class SupplierQuotationController extends MainController{
    private final RequestForQuotationService RFQService;
    private final SupplierQuotationService supplierQuotationService;
    @Autowired
    public SupplierQuotationController(SessionManager sessionManager, RequestForQuotationService RFQService, SupplierQuotationService supplierQuotationService) {
        super(sessionManager);
        this.RFQService = RFQService;
        this.supplierQuotationService = supplierQuotationService;
    }


    @GetMapping("/{id}/update-price")
    public String getSupplierQuoteRequests(@PathVariable String id, Model model, HttpSession session){
        String selectedSupplier = (String) session.getAttribute("supplier");
        if (selectedSupplier == null){
            return "redirect:/suppliers";
        }

        if(id == null)
            return String.format("redirect:/suppliers/%s/requests-for-quotation", selectedSupplier);

        SupplierQuotationItem supplierQuotationItem = supplierQuotationService.getSupplierQuotationItem(id);
        UpdateSupQuotaItemPriceFormData data = new UpdateSupQuotaItemPriceFormData();

        data.setId(supplierQuotationItem.getName());
        data.setPastPrice(supplierQuotationItem.getRate().doubleValue());
        data.setNewPrice(supplierQuotationItem.getRate().doubleValue());

        model.addAttribute("formData", data);
        return "devis/update-price";
    }

    @PostMapping("/items/{id}/update-price")
    public String updatePrice(@Validated @ModelAttribute("formData") UpdateSupQuotaItemPriceFormData data,
                              BindingResult bindingResult,
                              HttpSession session, Model model, RedirectAttributes redirectAttributes){
        try {
            if (bindingResult.hasErrors()){
                return "devis/update-price";
            }

            if(RFQService.updateSupplierQuotationPrice(data.getId(), data.getNewPrice())){
                redirectAttributes.addFlashAttribute("priceUpdatedAlert", "Price updated successfuly");
                return "redirect:/supplier-quotations";
            }
        } catch (AmountInvalidExcpetion | ActionNotAllowedExcpetion e) {
            model.addAttribute("error", e.getMessage());
            return getSupplierQuoteRequests(data.getId(), model, session);
        }

        return "devis/update-price";
    }

    @GetMapping()
    public String getSupplierQuoteRequests(Model model, HttpSession session){
        if (this.getSessionManager().getSessionCookie() == null){
            return "redirect:/login";
        }

        String selectedSupplier = (String) session.getAttribute("supplier");
        if (selectedSupplier == null){
            return "redirect:/suppliers";
        }

        List<SupplierQuotation> supplierQuotations = this.RFQService.getSupplierQuotation(selectedSupplier);
        model.addAttribute("quotations", supplierQuotations);
        return "devis/quotation-request";
    }


}
