package itu.mg.erpnext.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.UpdateSupQuotaItemPriceFormData;
import itu.mg.erpnext.exceptions.AmountInvalidExcpetion;
import itu.mg.erpnext.models.PurchaseOrder;
import itu.mg.erpnext.models.SupplierQuotationItem;
import itu.mg.erpnext.services.PurchaseOrderService;
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

import java.util.Arrays;
import java.util.List;

@Controller()
@RequestMapping("/purchase-orders")
public class PurchaseOrderController extends MainController{
    private final PurchaseOrderService purchaseOrderService;
    @Autowired
    public PurchaseOrderController(SessionManager sessionManager, PurchaseOrderService purchaseOrderService) {
        super(sessionManager);
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping()
    public String getSupplierQuoteRequests(@RequestParam(name = "status", required = false) List<String> status, Model model, HttpSession session) throws JsonProcessingException {
        if (this.getSessionManager().getSessionCookie() == null){
            return "redirect:/login";
        }

        String selectedSupplier = (String) session.getAttribute("supplier");

        if (selectedSupplier == null){
            return "redirect:/supplier-quotations";
        }

        List<String> statusList = purchaseOrderService.getPurchaseOrderStatus();
        List<PurchaseOrder> orders =  purchaseOrderService.getSupplierPurchaseOrders(selectedSupplier, status);

        model.addAttribute("orders", orders);
        model.addAttribute("status", statusList);
        model.addAttribute("selectedStatus", status);
        return "commande/list";
    }
}
