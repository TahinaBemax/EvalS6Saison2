package itu.mg.erpnext.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.PurchaseInvoiceResponse;
import itu.mg.erpnext.models.PurchaseInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class PurchaseInvoiceService extends MainService{
    public static final Logger logger = LoggerFactory.getLogger(PurchaseInvoiceService.class);
    public PurchaseInvoiceService(RestTemplateBuilder builder, SessionManager sessionManager) {
        super(builder, sessionManager);
    }

    public List<PurchaseInvoice> getSupplierPurchaseInvoice(String supplier_name) {
        try {
            String[] itemFields = {
                    "bill_date", "status","company","grand_total"
            };

            String[] status = {"Paid"};

            String resource = "Purchase Invoice";
            String filtre = String.format("[\"supplier\",\"=\",\"%s\"], [\"status\",\"in\", %s]", supplier_name, new ObjectMapper().writeValueAsString(status));

            String finalUrl = String.format("%s/api/resource/%s?filters=[%s]&fields=%s",
                    this.getErpNextUrl(), resource, filtre, new ObjectMapper().writeValueAsString(itemFields));

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie());

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<PurchaseInvoiceResponse> response = this.getRestTemplate().exchange(
                    finalUrl,
                    HttpMethod.GET,
                    entity,
                    PurchaseInvoiceResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()){
                return response.getBody().getData();
            }
        } catch (RestClientException | JsonProcessingException e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        throw new RuntimeException("An error occured when fetching Purchase Invoice");
    }
}
