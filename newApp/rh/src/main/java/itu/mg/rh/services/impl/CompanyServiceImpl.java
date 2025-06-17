package itu.mg.rh.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.rh.csv.service.impl.ImportCsvImpl;
import itu.mg.rh.models.Company;
import itu.mg.rh.services.CompanyService;
import itu.mg.rh.services.MainService;
import itu.mg.rh.utils.RestClientExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final MainService mainService;
    public static final Logger logger = LoggerFactory.getLogger(ImportCsvImpl.class);
    private String[] fields;

    public CompanyServiceImpl(MainService mainService) {
        this.mainService = mainService;
        this.fields = new String[] {
                "name", "company_name", "abbr", "default_currency", "country"
        };
    }

    @Override
    public void deleteByName(String name) {
        String url = String.format("%s/api/method/frappe.client.delete",
                this.mainService.getErpNextUrl());

        HttpHeaders headers = this.mainService.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("doctype", "Company");
        requestBody.put("name", name);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = this.mainService.getRestTemplate()
                    .exchange(url, HttpMethod.POST, entity, Map.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Unable to delete Company: " + name);
            }
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
    }

    @Override
    public boolean deleteAllCompany() {
        List<Company> all = this.findAll();
        try {
            for (Company company : all) {
                if (!company.getCompanyName().equalsIgnoreCase("IT University")){
                    this.deleteByName(company.getCompanyName());
                }
            }
        } catch (Exception e) {
            logger.error("DELETE COMPANY ERROR...", e);
            return false;

        }
        return true;
    }

    @Override
    public List<Company> findAll() {
        String url = String.format("%s/api/resource/Company?fields=%s",
                this.mainService.getErpNextUrl(), fieldAsString());

        HttpHeaders headers = this.mainService.getHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = this.mainService.getRestTemplate()
                    .exchange(url, HttpMethod.GET, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful())
                throw new RuntimeException("Unable to get Company Data");

            if (responseBody.containsKey("data")){
                List<Map> data = (List<Map>) responseBody.get("data");
                return data.stream()
                        .map(this::convertMapToCompany)
                        .toList();
            }
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }

        return null;
    }

    private Company convertMapToCompany(Map map) {
        Company company = new Company();
        company.setName((String) map.get("name"));
        company.setCompanyName((String) map.get("company_name"));
        company.setAbbr((String) map.get("abbr"));
        company.setDefaultCurrency((String) map.get("default_currency"));
        company.setCountry((String) map.get("country"));

        return company;
    }

    private String fieldAsString(){
        try {
            return new ObjectMapper().writeValueAsString(fields);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
