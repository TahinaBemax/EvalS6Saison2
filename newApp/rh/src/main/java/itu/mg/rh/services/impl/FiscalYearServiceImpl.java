package itu.mg.rh.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.rh.dto.ApiResponse;
import itu.mg.rh.models.FiscalYear;
import itu.mg.rh.services.FiscalYearService;
import itu.mg.rh.services.MainService;
import itu.mg.rh.utils.RestClientExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.*;

@Service
public class FiscalYearServiceImpl implements FiscalYearService {
    private final MainService mainService;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(FiscalYearServiceImpl.class);
    private static final String[] FISCAL_YEAR_FIELDS = {
        "name", "year", "year_start_date", "year_end_date", 
        "is_short_year"
    };

    @Autowired
    public FiscalYearServiceImpl(MainService mainService, ObjectMapper objectMapper) {
        this.mainService = mainService;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<FiscalYear> getAllFiscalYears() {
        String url = null;
        try {
            url = String.format("%s/api/resource/Fiscal Year?fields=%s",
                    mainService.getErpNextUrl(),
                    objectMapper.writeValueAsString(FISCAL_YEAR_FIELDS));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders headers = mainService.getHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = mainService.getRestTemplate().exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                List<Map> fiscalYearsData = (List<Map>) responseBody.get("data");
                return fiscalYearsData.stream()
                        .map(this::convertMapToFiscalYear)
                        .toList();
            }
        } catch (RestClientException e) {
            logger.error("Error fetching fiscal years: {}", e.getMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return Collections.emptyList();
    }

    @Override
    public FiscalYear getFiscalYearByDateAndCompany(LocalDate startDate, String company) {
        String fiscalYearName = String.format("%d-%d", startDate.getYear(), startDate.getYear() + 1);
        String url = String.format("%s/api/resource/Fiscal Year/%s", 
                mainService.getErpNextUrl(), 
                fiscalYearName);

        HttpHeaders headers = mainService.getHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = mainService.getRestTemplate().exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                FiscalYear fiscalYear = convertMapToFiscalYear(data);
                // Verify company matches
                if (fiscalYear.getCompany().equals(company)) {
                    return fiscalYear;
                }
            }
        } catch (RestClientException e) {
            logger.info("Fiscal year {} not found", fiscalYearName);
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    @Override
    public FiscalYear createFiscalYear(LocalDate startDate, String company) {
        // First check if fiscal year already exists
/*        FiscalYear existingFiscalYear = getFiscalYearByDateAndCompany(startDate, company);
        if (existingFiscalYear != null) {
            return existingFiscalYear;
        }*/

        // Create new fiscal year
        String fiscalYearName = String.format("%d-%d", startDate.getYear(), startDate.getYear() + 1);
        LocalDate endDate = startDate.plusYears(1).minusDays(1);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", fiscalYearName);
        requestBody.put("year", fiscalYearName);
        requestBody.put("year_start_date", startDate.toString());
        requestBody.put("year_end_date", endDate.toString());
        requestBody.put("is_short_year", false);
        //requestBody.put("company", company);
        requestBody.put("is_closed", false);

        HttpHeaders headers = mainService.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> createRequestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> createResponse = mainService.getRestTemplate().exchange(
                    mainService.getErpNextUrl() + "/api/resource/Fiscal Year",
                    HttpMethod.POST,
                    createRequestEntity,
                    Map.class
            );

            if (createResponse.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> data = (Map<String, Object>) createResponse.getBody().get("data");
                return convertMapToFiscalYear(data);
            } else {
                throw new RuntimeException("Failed to create fiscal year: " + createResponse.getBody());
            }
        } catch (RestClientException e) {
            logger.error("Error creating fiscal year: {}", e.getMessage());
            RestClientExceptionHandler.handleError(e);
        }

        return null;
    }

    private FiscalYear convertMapToFiscalYear(Map<String, Object> data) {
        FiscalYear fiscalYear = new FiscalYear();
        fiscalYear.setName((String) data.get("name"));
        fiscalYear.setYear((String) data.get("year"));
        fiscalYear.setYearStartDate(LocalDate.parse((String) data.get("year_start_date")));
        fiscalYear.setYearEndDate(LocalDate.parse((String) data.get("year_end_date")));
        fiscalYear.setIsShortYear((Integer) data.get("is_short_year"));
        return fiscalYear;
    }
} 