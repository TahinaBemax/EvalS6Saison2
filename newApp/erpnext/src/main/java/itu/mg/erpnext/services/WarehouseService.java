package itu.mg.erpnext.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.WarehouseResponse;
import itu.mg.erpnext.models.Warehouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class WarehouseService extends MainService{
    public static final Logger logger = LoggerFactory.getLogger(SupplierQuotationService.class);
    public WarehouseService(RestTemplateBuilder restTemplate, SessionManager sessionManager) {
        super(restTemplate, sessionManager);
    }

    public List<Warehouse> getWarehouses() throws JsonProcessingException {
        String[] fields = {"name", "warehouse_name"};
        String resource = "Warehouse";
        String url = String.format("%s/api/resource/%s?fields=%s", this.getErpNextUrl(), resource, new ObjectMapper().writeValueAsString(fields));

        HttpHeaders headers = this.getHeaders();

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<WarehouseResponse> response = this.getRestTemplate()
                    .exchange(url, HttpMethod.GET, entity, WarehouseResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody().getData();
            }
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        return null;
    }
}
