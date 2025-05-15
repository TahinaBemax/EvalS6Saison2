package itu.mg.erpnext.controller;

import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.ApiResponse;
import itu.mg.erpnext.dto.SupplierQuotationDTO;
import itu.mg.erpnext.dto.UpdateSupQuotaItemPriceFormData;
import itu.mg.erpnext.exceptions.AccountCompanyNotFoundExcpetion;
import itu.mg.erpnext.exceptions.ActionNotAllowedExcpetion;
import itu.mg.erpnext.exceptions.AmountInvalidExcpetion;
import itu.mg.erpnext.models.Item;
import itu.mg.erpnext.models.Supplier;
import itu.mg.erpnext.models.SupplierQuotation;
import itu.mg.erpnext.models.SupplierQuotationItem;
import itu.mg.erpnext.services.ItemService;
import itu.mg.erpnext.services.RequestForQuotationService;
import itu.mg.erpnext.services.SupplierQuotationService;
import itu.mg.erpnext.services.SupplierService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller()
@RequestMapping("/supplier-quotations")
public class SupplierQuotationController extends MainController {
    private final RequestForQuotationService RFQService;
    private final SupplierQuotationService supplierQuotationService;
    private final ItemService itemService;
    private final SupplierService supplierService;

    @Autowired
    public SupplierQuotationController(SessionManager sessionManager,
                                       RequestForQuotationService RFQService,
                                       SupplierQuotationService supplierQuotationService,
                                       ItemService itemService,
                                       SupplierService supplierService) {
        super(sessionManager);
        this.RFQService = RFQService;
        this.supplierQuotationService = supplierQuotationService;
        this.itemService = itemService;
        this.supplierService = supplierService;
    }

    @GetMapping("/new")
    public String pageSupplierQuotationInsertion(Model model, HttpSession session) {
        if (this.getSessionManager().getSessionCookie() == null) {
            return "redirect:/login";
        }

        String selectedSupplier = (String) session.getAttribute("supplier");

        model.addAttribute("supplier", selectedSupplier);
        SupplierQuotationDTO supplierQuotationDTO = new SupplierQuotationDTO();
        supplierQuotationDTO.setSupplier(selectedSupplier);

        model.addAttribute("supplierQuotation", supplierQuotationDTO);
        return "devis/form";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<?> save(@Validated @RequestBody SupplierQuotationDTO supplierQuotationDTO,
                       BindingResult bindingResult)
    {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(List.of(supplierQuotationDTO), "Bad Request!", "error"));
        }

        if (supplierQuotationService.save(supplierQuotationDTO)) {
            return ResponseEntity
                    .ok(new ApiResponse<>(List.of(supplierQuotationDTO), "Saved!", "success"));
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(List.of(supplierQuotationDTO), "Internal Server Error", "error"));
    }


    @GetMapping("/{id}/update-price")
    public String getSupplierQuoteRequests(@PathVariable String id, Model model, HttpSession session) {
        String selectedSupplier = (String) session.getAttribute("supplier");
        if (selectedSupplier == null) {
            return "redirect:/suppliers";
        }

        if (id == null)
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
                              HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                return "devis/update-price";
            }

            if (RFQService.updateSupplierQuotationPrice(data.getId(), data.getNewPrice())) {
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
    public String getSupplierQuoteRequests(Model model, HttpSession session) {
        if (this.getSessionManager().getSessionCookie() == null) {
            return "redirect:/login";
        }

        String selectedSupplier = (String) session.getAttribute("supplier");
        if (selectedSupplier == null) {
            return "redirect:/suppliers";
        }

        List<SupplierQuotation> supplierQuotations = this.RFQService.getSupplierQuotation(selectedSupplier);
        model.addAttribute("quotations", supplierQuotations);
        return "devis/quotation-request";
    }


}
