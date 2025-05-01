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
public class QuotationService extends MainService{
    public QuotationService(RestTemplateBuilder builder, SessionManager sessionManager) {
        super(builder, sessionManager);
    }

    public SupplierResponse getSupplierQuotation(String supplierName) {
        String[] fields = {"name", "customer_name", "transaction_date", "order_type", "company", "total_qty", "base_total"};
        String url = String.format("%s/api/resource/Quotation?fields=[%s]&filters=[]", this.getErpNextUrl(), fields.toString() );

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie()); // Utilise le cookie de session

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
