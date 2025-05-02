package itu.mg.erpnext.services;

import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.SupplierResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SupplierService extends MainService{
    public SupplierService(RestTemplateBuilder builder, SessionManager sessionManager) {
        super(builder, sessionManager);
    }

    public SupplierResponse getSuppliers() {
        String url = this.getErpNextUrl() + "/api/resource/Supplier?fields=[\"name\", \"supplier_name\"]";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<SupplierResponse> response = this.getRestTemplate().exchange(
                url,
                HttpMethod.GET,
                entity,
                SupplierResponse.class
        );

        return response.getBody();
    }
}
