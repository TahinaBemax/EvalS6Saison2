package itu.mg.erpnext.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.PurchaseOrderResponse;
import itu.mg.erpnext.dto.SupplierQuotationItemResponse;
import itu.mg.erpnext.exceptions.RequestForQuotationException;
import itu.mg.erpnext.exceptions.SupplierQuotationItemNotFoundException;
import itu.mg.erpnext.models.PurchaseOrder;
import itu.mg.erpnext.models.SupplierQuotationItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseOrderService extends MainService{
    public static final Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);
    public PurchaseOrderService(RestTemplateBuilder builder, SessionManager sessionManager) {
        super(builder, sessionManager);
    }

    public boolean updateSupplierQuotationPrice(String itemId, double newPrice) {
        // Préparation des en-têtes avec cookie de session
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie());

        // Corps de la requête avec le nouveau prix
        Map<String, Object> body = new HashMap<>();
        body.put("rate", newPrice);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // Construction de l’URL du document ERPNext à modifier (basé sur son "name")
        String updateUrl = String.format("%s/api/resource/Supplier Quotation Item/%s", this.getErpNextUrl(), itemId);

        // Exécution de la requête PUT
        ResponseEntity<String> response = this.getRestTemplate().exchange(
                updateUrl,
                HttpMethod.PUT,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return true;
        } else {
            throw new RuntimeException("An error occured wheh updating supplier quotation price");
        }
    }


    public List<PurchaseOrder> getSupplierPurchaseOrders(String supplier_name) {
        try {
            String[] itemFields = {
                    "name", "transaction_date", "status","company","items.item_code", "items.item_name", "items.qty", "items.rate", "items.amount"
            };

            String[] status = {"Received", "Completed"};

            String resource = "Purchase Order";
            String filtre = String.format("[\"supplier\",\"=\",\"%s\"], [\"status\",\"in\", %s]", supplier_name, new ObjectMapper().writeValueAsString(status));

            String finalUrl = String.format("%s/api/resource/%s?filters=[%s]&fields=%s",
                    this.getErpNextUrl(), resource, filtre, new ObjectMapper().writeValueAsString(itemFields));

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie());

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<PurchaseOrderResponse> response = this.getRestTemplate().exchange(
                    finalUrl,
                    HttpMethod.GET,
                    entity,
                    PurchaseOrderResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()){
                return response.getBody().getData();
            }
        } catch (RestClientException | JsonProcessingException e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        throw new RuntimeException("An error occured when fetching Purchase Orders");
    }
}
