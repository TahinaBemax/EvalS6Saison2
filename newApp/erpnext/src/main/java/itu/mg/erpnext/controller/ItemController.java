package itu.mg.erpnext.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.ApiResponse;
import itu.mg.erpnext.models.Item;
import itu.mg.erpnext.models.ItemGroup;
import itu.mg.erpnext.services.ItemGroupService;
import itu.mg.erpnext.services.SupplierQuotationService;
import itu.mg.erpnext.services.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemController extends MainController{
    private final ItemService itemService;
    private final ItemGroupService itemGroupService;
    public static final Logger logger = LoggerFactory.getLogger(SupplierQuotationService.class);

    @Autowired
    public ItemController(SessionManager sessionManager, ItemService itemService, ItemGroupService itemGroupService) {
        super(sessionManager);
        this.itemService = itemService;
        this.itemGroupService = itemGroupService;
    }



    @GetMapping("list")
    public String listItems(Model model){
        if (this.getSessionManager().getSessionCookie() == null) {
            return "redirect:/login";
        }

        try {
            List<Item> items = itemService.getItems();
            List<ItemGroup> itemGroups = itemGroupService.getItemGroups();
            model.addAttribute("itemGroups", itemGroups);
            model.addAttribute("items", items);
            return "item/index";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<?> getItems(){
        if (this.getSessionManager().getSessionCookie() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, "Need Authentication", "error"));
        }

        List<Item> items = null;
        try {
            items = itemService.getItems();
            if (items != null) {
                return ResponseEntity.ok(new ApiResponse<>(items, "", "success"));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(new ApiResponse<>(items, "An error occured when fetching items data.", "error"));
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> save(@RequestBody Item item){
        boolean response = this.itemService.save(item);

        if (response){
            return ResponseEntity.ok(new ApiResponse<>(null, "Saved successfuly", "success"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
