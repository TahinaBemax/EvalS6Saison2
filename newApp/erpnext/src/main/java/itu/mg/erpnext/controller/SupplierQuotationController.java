package itu.mg.erpnext.controller;

import itu.mg.erpnext.components.SessionManager;
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
public class SupplierQuotationController extends MainController{
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
    public String pageSupplierQuotationInsertion(Model model){
        List<Item>  items = itemService.getItems().getData();
        List<Supplier>  suppliers = supplierService.getSuppliers().getData();

        model.addAttribute("items", items);
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("supplierQuotation", new SupplierQuotationDTO());
        return "devis/form";
    }

    @GetMapping("/save")
    public String save(){
        try {
            SupplierQuotationDTO dto = new SupplierQuotationDTO();
            dto.setSupplier_name("ABC");

            List<Item> items = new ArrayList<>();
            Item item1 = new Item();
            item1.setItem_code("ITEM-00016");
            item1.setAmount(BigDecimal.valueOf(2));
            item1.setRate(BigDecimal.valueOf(2));
            item1.setItem_name("tuile");
            item1.setUom("Abampere");
            item1.setWarehouse("Stores - ITU");
            item1.setQty(5);

            Item item2 = new Item();
            item2.setItem_code("ITEM-00015");
            item2.setAmount(BigDecimal.valueOf(2));
            item2.setRate(BigDecimal.valueOf(2));
            item2.setItem_name("colle");
            item2.setUom("Abampere");
            item2.setWarehouse("Stores - ITU");
            item2.setQty(11);

            Item item3 = new Item();
            item3.setItem_code("ITEM-00015");
            item3.setAmount(BigDecimal.valueOf(221));
            item3.setRate(BigDecimal.valueOf(211));
            item3.setItem_name("colle");
            item3.setUom("Abampere");
            item3.setWarehouse("Stores - ITU");
            item3.setQty(230);

            items.add(item1);
            items.add(item2);
            items.add(item3);

            dto.setItems(items);

            supplierQuotationService.save(dto);
        } catch (AccountCompanyNotFoundExcpetion e) {
            throw new RuntimeException(e);
        }

        return "redirect:/supplier-quotations";
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
