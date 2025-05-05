package itu.mg.erpnext.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.AccountListResponse;
import itu.mg.erpnext.dto.PurchaseInvoiceResponse;
import itu.mg.erpnext.dto.SinglePurchaseInvoiceResponse;
import itu.mg.erpnext.exceptions.AccountCompanyNotFoundExcpetion;
import itu.mg.erpnext.models.Account;
import itu.mg.erpnext.models.PurchaseInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseInvoiceService extends MainService{
    public static final Logger logger = LoggerFactory.getLogger(PurchaseInvoiceService.class);
    private static final String[] fields = {
            "name","bill_date", "status","company","grand_total","supplier", "credit_to"
    };

    public PurchaseInvoiceService(RestTemplateBuilder builder, SessionManager sessionManager) {
        super(builder, sessionManager);
    }

    public List<PurchaseInvoice> getSupplierPurchaseInvoice(String supplier_name) {
        try {

            String[] status = {"Paid"};

            String resource = "Purchase Invoice";
            //String filtre = String.format("[\"supplier\",\"=\",\"%s\"], [\"status\",\"in\", %s]", supplier_name, new ObjectMapper().writeValueAsString(status));

            String finalUrl = String.format("%s/api/resource/%s?fields=%s",
                    this.getErpNextUrl(), resource, new ObjectMapper().writeValueAsString(fields));

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
    public PurchaseInvoice getPurchaseInvoiceDetails(String invoiceName) throws Exception {
        try {

            String url = String.format("%s/api/resource/Purchase Invoice/%s?fields=%s",
                    this.getErpNextUrl(),
                    invoiceName,
                    new ObjectMapper().writeValueAsString(fields));

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie());

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<SinglePurchaseInvoiceResponse> response = this.getRestTemplate().exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    SinglePurchaseInvoiceResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody().getData();
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de la facture : " + e.getMessage());
            throw new Exception(e);
        }
        return null;
    }
/*
    public String paySupplierPurchaseInvoice(String invoiceName, BigDecimal amount) throws AccountCompanyNotFoundExcpetion {
        try {
            String url = String.format("%s/api/resource/Payment Entry", this.getErpNextUrl());

            // 1. On récupère les infos de la facture (pour obtenir le fournisseur, la company, etc.)
            PurchaseInvoice invoice = getPurchaseInvoiceDetails(invoiceName);
            if (invoice == null) {
                throw new RuntimeException("Invoice not found: " + invoiceName);
            }
            Account account = getCompanyPaymentAccount(invoice.getCompany());

            if (account == null){
                throw new AccountCompanyNotFoundExcpetion(invoice.getCompany());
            }

            // 2. Création du corps de la requête pour Payment Entry
            Map<String, Object> data = new HashMap<>();
            data.put("doctype", "Payment Entry");
            data.put("payment_type", "Pay");
            data.put("party_type", "Supplier");
            data.put("party", invoice.getSupplier());
            data.put("company", invoice.getCompany());
            data.put("paid_amount", amount);
            data.put("reference_doctype", "Purchase Invoice");
            data.put("reference_name", invoiceName);
            data.put("posting_date", java.time.LocalDate.now().toString());
            data.put("mode_of_payment", "Cash"); // À ajuster selon tes données
            data.put("received_amount", amount);
            data.put("paid_to", account.getName()); // ou "Bank" si c’est un compte bancaire
            data.put("source_exchange_rate", 1); // Ajout requis
            // Champs obligatoires ajoutés :
            data.put("paid_from", account.getName());
            data.put("paid_from_account_currency", account.getAccount_currency());


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(data, headers);

            ResponseEntity<String> response = this.getRestTemplate().exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return "Payment successfully created: " + response.getBody();
            }
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException("Error paying invoice: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Failed to create Payment Entry");
    }
*/

    public String paySupplierPurchaseInvoice(String invoiceName, BigDecimal amount) throws AccountCompanyNotFoundExcpetion {
        try {
            String url = String.format("%s/api/resource/Payment Entry", this.getErpNextUrl());

            // 1. On récupère les infos de la facture
            PurchaseInvoice invoice = getPurchaseInvoiceDetails(invoiceName);
            if (invoice == null) {
                throw new RuntimeException("Invoice not found: " + invoiceName);
            }

            Account account = getCompanyPaymentAccount(invoice.getCompany());
            if (account == null) {
                throw new AccountCompanyNotFoundExcpetion(invoice.getCompany());
            }

            // 2. Création du corps de la requête
            Map<String, Object> data = new HashMap<>();
            data.put("doctype", "Payment Entry");
            data.put("payment_type", "Pay");
            data.put("party_type", "Supplier");
            data.put("party", invoice.getSupplier());
            data.put("company", invoice.getCompany());
            data.put("posting_date", java.time.LocalDate.now().toString());
            data.put("mode_of_payment", "Cash");
            data.put("paid_from", account.getName());
            data.put("paid_from_account_currency", account.getAccount_currency());
            data.put("paid_to", invoice.getCredit_to()); // Tu peux ajuster si tu as un compte "Cash" ou "Bank"
            data.put("source_exchange_rate", 1);
            data.put("paid_amount", amount);
            data.put("received_amount", amount);
            data.put("submit_after_save", true);

            // ✅ Partie manquante : ajout de la section "references"
            List<Map<String, Object>> references = new ArrayList<>();
            Map<String, Object> reference = new HashMap<>();
            reference.put("reference_doctype", "Purchase Invoice");
            reference.put("reference_name", invoiceName);
            reference.put("total_amount", invoice.getGrand_total()); // À adapter selon ton modèle
            reference.put("outstanding_amount", invoice.getOutstanding_amount()); // À adapter aussi
            reference.put("allocated_amount", amount); // Ce que tu paies ici

            references.add(reference);
            data.put("references", references);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(data, headers);

            ResponseEntity<String> response = this.getRestTemplate().exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return "Payment successfully created: " + response.getBody();
            }

        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException("Error paying invoice: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Failed to create Payment Entry");
    }

    public Account getCompanyPaymentAccount(String company) {
        try {
            String resource = "Account";
            String[] fields = { "name", "account_type", "company", "account_currency" };

            String filters = String.format(
                    "[[\"company\", \"=\", \"%s\"], [\"account_type\", \"=\", \"Cash\"], [\"is_group\", \"=\", 0]]",
                    company
            );

            String url = String.format("%s/api/resource/%s?filters=%s&fields=%s",
                    this.getErpNextUrl(),
                    resource,
                    filters,
                    new ObjectMapper().writeValueAsString(fields));

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie());

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<AccountListResponse> response = this.getRestTemplate().exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    AccountListResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                List<Account> accounts = response.getBody().getData();
                if (!accounts.isEmpty()) {
                    // Retourne le premier compte valide
                    return accounts.get(0);
                } else {
                    throw new RuntimeException("No cash/bank account found for company: " + company);
                }
            }

        } catch (Exception e) {
            logger.error("Error fetching payment account: " + e.getMessage());
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Could not retrieve payment account");
    }

    private double resteAPayer(double total, double amount){
        if (total < amount)
            return 0;
        return total - amount;
    }

}
