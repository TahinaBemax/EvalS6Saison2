package itu.mg.erpnext.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.ApiResponse;
import itu.mg.erpnext.models.Item;
import itu.mg.erpnext.models.Warehouse;
import itu.mg.erpnext.services.SupplierQuotationService;
import itu.mg.erpnext.services.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemRestController extends MainController{
    private final ItemService itemService;
    public static final Logger logger = LoggerFactory.getLogger(SupplierQuotationService.class);

    @Autowired
    public ItemRestController(SessionManager sessionManager, ItemService itemService) {
        super(sessionManager);
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<?> getItems() throws JsonProcessingException{
        if (this.getSessionManager().getSessionCookie() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, "Need Authentication", "error"));
        }

        List<Item> items = itemService.getItems();
        if (items != null) {
            return ResponseEntity.ok(new ApiResponse<>(items, "", "success"));
        }

        return ResponseEntity.ok(new ApiResponse<>(items, "An error occured when fetching items data.", "error"));
    }
}
