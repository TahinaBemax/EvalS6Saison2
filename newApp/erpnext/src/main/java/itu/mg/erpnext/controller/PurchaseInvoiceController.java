package itu.mg.erpnext.controller;

import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.exceptions.AccountCompanyNotFoundExcpetion;
import itu.mg.erpnext.models.PurchaseInvoice;
import itu.mg.erpnext.services.PurchaseInvoiceService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller()
@RequestMapping("/purchase-invoices")
public class PurchaseInvoiceController extends MainController{
    private final PurchaseInvoiceService purchaseInvoiceService;
    public static final Logger logger = LoggerFactory.getLogger(PurchaseInvoiceController.class);

    @Autowired
    public PurchaseInvoiceController(SessionManager sessionManager, PurchaseInvoiceService purchaseInvoiceService) {
        super(sessionManager);
        this.purchaseInvoiceService = purchaseInvoiceService;
    }

    @GetMapping()
    public String getPurchaseInvoices(@RequestParam(name = "status", required = false) String status, Model model, HttpSession session){
        if (this.getSessionManager().getSessionCookie() == null){
            return "redirect:/login";
        }

        String selectedSupplier = (String) session.getAttribute("supplier");
        status = "Received";

/*        if (selectedSupplier == null){
            return "redirect:/supplier-quotations";
        }*/

        List<PurchaseInvoice> orders =  purchaseInvoiceService.getSupplierPurchaseInvoice();
        model.addAttribute("factures", orders);
        return "facture/list";
    }

    @GetMapping("/{name}/bill")
    public String getBillForm(@PathVariable String name, Model model){
        if (this.getSessionManager().getSessionCookie() == null){
            return "redirect:/login";
        }
        if (name == null){
            return "redirect:/purchase-invoices";
        }

        PurchaseInvoice invoice = null;
        try {
            invoice = purchaseInvoiceService.getPurchaseInvoiceDetails(name);
            model.addAttribute("invoice", invoice);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            model.addAttribute("error", "Error server");
            throw new RuntimeException(e);
        }
        model.addAttribute("facture", invoice);
        return "facture/bill-form";
    }

    @PostMapping()
    public String billPurchaseInvoices(@RequestParam(name = "name") String name, @RequestParam(name = "amount")
                                       BigDecimal amount, RedirectAttributes redirectAttributes){
        if (this.getSessionManager().getSessionCookie() == null){
            return "redirect:/login";
        }

        if (name == null){
            return "redirect:/purchase-invoices";
        }

        if (amount == null || amount.compareTo(BigDecimal.valueOf(0)) <= 0){
            redirectAttributes.addFlashAttribute("ErrorAmount", "Amount must be more than zero");
            return String.format("redirect:/%s/bill", name);
        }

        try {
            if (purchaseInvoiceService.paySupplierPurchaseInvoice(name, amount) != null) {
                redirectAttributes.addFlashAttribute("billSuccess", "Facture paid");
            } else {
                redirectAttributes.addFlashAttribute("billError", "An error occured when billing the facture");
            }
        } catch (AccountCompanyNotFoundExcpetion e) {
            logger.error(e.getLocalizedMessage());
            redirectAttributes.addFlashAttribute("billError", e.getMessage());
        }

        return "redirect:/purchase-invoices";
    }
}
