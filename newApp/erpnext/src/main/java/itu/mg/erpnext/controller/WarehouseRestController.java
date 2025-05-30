package itu.mg.erpnext.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.ApiResponse;
import itu.mg.erpnext.models.Warehouse;
import itu.mg.erpnext.services.SupplierQuotationService;
import itu.mg.erpnext.services.WarehouseService;
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
@RequestMapping("/warehouses")
public class WarehouseRestController extends MainController{
    private final WarehouseService warehouseService;
    public static final Logger logger = LoggerFactory.getLogger(SupplierQuotationService.class);

    @Autowired
    public WarehouseRestController(SessionManager sessionManager, WarehouseService warehouseService) {
        super(sessionManager);
        this.warehouseService = warehouseService;
    }



    @GetMapping
    public ResponseEntity<?> getWarehouses() throws JsonProcessingException{
        List<Warehouse> warehouses = null;
        try {
            if (this.getSessionManager().getSessionCookie() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(null, "Need Authentication", "error"));
            }

            warehouses = warehouseService.getWarehouses();
        } catch (JsonProcessingException e) {
            logger.error(e.getLocalizedMessage());
            throw e;
        }
        return ResponseEntity.ok(new ApiResponse<>(warehouses, "", "success"));
    }
}
