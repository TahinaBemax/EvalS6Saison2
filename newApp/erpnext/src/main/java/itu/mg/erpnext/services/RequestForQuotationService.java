package itu.mg.erpnext.services;

import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.QuotationSupplierResponse;
import itu.mg.erpnext.dto.RequestForQuotationResponse;
import itu.mg.erpnext.dto.RequestForQuotationSupplierResponse;
import itu.mg.erpnext.exceptions.RequestForQuotationException;
import itu.mg.erpnext.models.RequestForQuotation;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestForQuotationService extends MainService{
    public RequestForQuotationService(RestTemplateBuilder builder, SessionManager sessionManager) {
        super(builder, sessionManager);
    }


    public QuotationSupplierResponse updateSupplierQuotationPrice(String name, double newPrice) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // 1. Obtenir les parents depuis la table enfant
        String childUrl = String.format("%s/api/resource/Supplier Quotation",
                this.getErpNextUrl());

        ResponseEntity<QuotationSupplierResponse> childResponse = this.getRestTemplate().exchange(
                childUrl,
                HttpMethod.PUT,
                entity,
                QuotationSupplierResponse.class
        );


        return childResponse.getBody(); // vide

    }
    public List<RequestForQuotation> getSupplierQuotation(String supplierName) {
        String finalUrl = String.format("%s/api/method/custom_app.api_rest.api.get_rfq_by_supplier?supplier_name=%s",
                this.getErpNextUrl(), supplierName
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie()); // Utilise le cookie de session

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<RequestForQuotationResponse> response = this.getRestTemplate().exchange(
                finalUrl,
                HttpMethod.GET,
                entity,
                RequestForQuotationResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody().getMessage();
        }

        throw new RequestForQuotationException("An error occured when fetching Request for quotations");
    }
}
