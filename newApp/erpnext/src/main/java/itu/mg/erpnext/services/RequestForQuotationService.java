package itu.mg.erpnext.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.SupplierQuotationResponse;
import itu.mg.erpnext.exceptions.ActionNotAllowedExcpetion;
import itu.mg.erpnext.exceptions.AmountInvalidExcpetion;
import itu.mg.erpnext.exceptions.RequestForQuotationException;
import itu.mg.erpnext.models.SupplierQuotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.*;

@Service
public class RequestForQuotationService extends MainService {
    public static final Logger logger = LoggerFactory.getLogger(RequestForQuotationService.class);
    private final String[] sqFields;
    private final String[] itemFields;

    public RequestForQuotationService(RestTemplateBuilder builder, SessionManager sessionManager) {
        super(builder, sessionManager);
        this.sqFields = new String[] {
            "transaction_date", "company", "supplier", "supplier_name", "total_qty", "docstatus"
        };
        this.itemFields = new String[] {
                "items.name","items.item_code", "items.item_name", "items.qty", "items.rate", "items.amount"
        };
    }


    public boolean updateSupplierQuotationPrice(String itemId, double newPrice) {
        if (newPrice < 0){
            throw new AmountInvalidExcpetion(newPrice);
        }

        // Preparation headers with session cookie
        HttpHeaders headers = this.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Corps de la requête avec le nouveau prix
        Map<String, Object> body = new HashMap<>();
        body.put("rate", newPrice);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // Construction de l’URL du document ERPNext à modifier (basé sur son "name")
        String updateUrl = String.format("%s/api/resource/Supplier Quotation Item/%s", this.getErpNextUrl(), itemId);

        // Exécution de la requête PUT
        ResponseEntity<Map> response = this.getRestTemplate().exchange(
                updateUrl,
                HttpMethod.PUT,
                entity,
                Map.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return true;
        } else if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(417))) {
            throw new ActionNotAllowedExcpetion();
        } else {
            throw new RuntimeException("An error occured wheh updating supplier quotation price");
        }
    }

    public List<SupplierQuotation> getSupplierQuotation(String supplierName) {
        try {
            if (supplierName == null)
                throw new RuntimeException("Supplier name is null");

            // Fusion des deux ensembles
            List<String> allFields = new ArrayList<>();
            allFields.addAll(Arrays.asList(sqFields));
            allFields.addAll(Arrays.asList(itemFields));

            String resource = "Supplier Quotation";
            String finalUrl = String.format("%s/api/resource/%s?filters=[[\"supplier\",\"=\",\"%s\"]]&fields=%s",
                    this.getErpNextUrl(), resource, supplierName, new ObjectMapper().writeValueAsString(allFields));

            HttpHeaders headers = this.getHeaders(); // Utilise le cookie de session
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<SupplierQuotationResponse> response = this.getRestTemplate().exchange(
                    finalUrl,
                    HttpMethod.GET,
                    entity,
                    SupplierQuotationResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()){
                return response.getBody().getData();
            }
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        throw new RequestForQuotationException("An error occured when fetching Request for quotations");
    }
}
//String finalUrl = String.format("%s/api/method/custom_app.api_rest.api.get_rfq_by_supplier?supplier_name=%s", this.getErpNextUrl(), supplierName);

