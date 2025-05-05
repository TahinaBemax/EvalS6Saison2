package itu.mg.erpnext.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.SupplierQuotationItemResponse;
import itu.mg.erpnext.exceptions.RequestForQuotationException;
import itu.mg.erpnext.exceptions.SupplierQuotationItemNotFoundException;
import itu.mg.erpnext.models.SupplierQuotationItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.*;

@Service
public class SupplierQuotationService extends MainService{
    public static final Logger logger = LoggerFactory.getLogger(SupplierQuotationService.class);
    public SupplierQuotationService(RestTemplateBuilder builder, SessionManager sessionManager) {
        super(builder, sessionManager);
    }

    public SupplierQuotationItem getSupplierQuotationItem(String name) {
        try {
            String[] itemFields = {
                    "name","item_code", "item_name", "qty", "rate", "amount"
            };

            String resource = "Supplier Quotation Item";
            String finalUrl = String.format("%s/api/resource/%s/%s?fields=%s",
                    this.getErpNextUrl(), resource, name, new ObjectMapper().writeValueAsString(itemFields));

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie());

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<SupplierQuotationItemResponse> response = this.getRestTemplate().exchange(
                    finalUrl,
                    HttpMethod.GET,
                    entity,
                    SupplierQuotationItemResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()){
                if (response.getBody().getData() == null)
                    throw new SupplierQuotationItemNotFoundException(name);

                return response.getBody().getData();
            }
        } catch (RestClientException | JsonProcessingException e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        throw new RequestForQuotationException("An error occured when fetching Request for quotation Item");
    }
}
