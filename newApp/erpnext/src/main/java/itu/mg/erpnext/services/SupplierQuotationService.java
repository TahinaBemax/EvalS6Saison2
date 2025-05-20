package itu.mg.erpnext.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.SupplierQuotationDTO;
import itu.mg.erpnext.dto.SupplierQuotationItemResponse;
import itu.mg.erpnext.exceptions.AccountCompanyNotFoundExcpetion;
import itu.mg.erpnext.exceptions.RequestForQuotationException;
import itu.mg.erpnext.exceptions.SupplierQuotationItemNotFoundException;
import itu.mg.erpnext.models.Account;
import itu.mg.erpnext.models.Item;
import itu.mg.erpnext.models.PurchaseInvoice;
import itu.mg.erpnext.models.SupplierQuotationItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    public boolean save(SupplierQuotationDTO data){
        try {
            String url = String.format("%s/api/resource/Supplier Quotation", this.getErpNextUrl());

            Map<String, Object> formdata = this.prepareData(data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(formdata, headers);


            ResponseEntity<String> stringResponseEntity = this.getRestTemplate().postForEntity(url, entity, String.class);

            return stringResponseEntity.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object>  prepareData(SupplierQuotationDTO supplierQuotation){
        Map<String, Object> data = new HashMap<>();
        data.put("series", supplierQuotation.getSeries());
        data.put("supplier", supplierQuotation.getSupplier());
        data.put("company", "IT University");
        data.put("Date", supplierQuotation.getTransaction_date());
        data.put("submit_after_save", true);


        // ✅ Partie manquante : ajout de la section "references"
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (Item item : supplierQuotation.getItems()) {
            Map<String, Object> items = new HashMap<>();
            items.put("exchanger_rate", 0);
            items.put("item_code", item.getItem_code());
            items.put("amount", item.getAmount());
            items.put("qty", item.getQty());
            items.put("uom", item.getStock_uom()); // Tu peux ajuster si tu as un compte "Cash" ou "Bank"
            items.put("rate", item.getRate());
            items.put("warehouse", item.getWarehouse());
            itemList.add(items);
        }


        data.put("items", itemList);

        return data;
    }

}
