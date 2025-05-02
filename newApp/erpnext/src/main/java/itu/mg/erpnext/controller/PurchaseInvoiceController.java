package itu.mg.erpnext.controller;

import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.models.PurchaseInvoice;
import itu.mg.erpnext.services.PurchaseInvoiceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller()
@RequestMapping("/purchase-invoices")
public class PurchaseInvoiceController extends MainController{
    private final PurchaseInvoiceService purchaseInvoiceService;
    @Autowired
    public PurchaseInvoiceController(SessionManager sessionManager, PurchaseInvoiceService purchaseInvoiceService) {
        super(sessionManager);
        this.purchaseInvoiceService = purchaseInvoiceService;
    }

    @GetMapping()
    public String getSupplierQuoteRequests(@RequestParam(name = "status", required = false) String status, Model model, HttpSession session){
        if (this.getSessionManager().getSessionCookie() == null){
            return "redirect:/login";
        }

        String selectedSupplier = (String) session.getAttribute("supplier");
        status = "Received";

        if (selectedSupplier == null){
            return "redirect:/supplier-quotations";
        }

        List<PurchaseInvoice> orders =  purchaseInvoiceService.getSupplierPurchaseInvoice(selectedSupplier);
        model.addAttribute("factures", orders);
        return "facture/list";
    }
}
