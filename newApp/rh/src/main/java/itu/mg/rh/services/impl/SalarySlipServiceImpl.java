package itu.mg.rh.services.impl;

import itu.mg.rh.components.SessionManager;
import itu.mg.rh.csv.service.ImportCsv;
import itu.mg.rh.dto.SalarySlipDTO;
import itu.mg.rh.exception.FrappeApiException;
import itu.mg.rh.services.MainService;
import itu.mg.rh.services.SalarySlipService;
import itu.mg.rh.utils.RestClientExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class SalarySlipServiceImpl implements SalarySlipService {
    
    @Value("${erpnext.url}")
    private String erpNextUrl;
    
    private final RestTemplate restTemplate;
    private final SessionManager sessionManager;
    public static final Logger logger = LoggerFactory.getLogger(ImportCsv.class);

    @Autowired
    public SalarySlipServiceImpl(RestTemplateBuilder restTemplate , SessionManager sessionManager) {
        this.restTemplate = restTemplate.build();
        this.sessionManager = sessionManager;
    }

    @Override
    public SalarySlipDTO getSalarySlipById(String salarySlipId) {
        String url = erpNextUrl + "/api/resource/Salary Slip/" + salarySlipId;
        
        HttpHeaders headers = getHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<SalarySlipDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                SalarySlipDTO.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Salary slip not found with ID: " + salarySlipId);
        }
    }

    @Override
    public List<SalarySlipDTO> getEmployeeSalarySlips(String employeeId) {
        String url = erpNextUrl + "/api/resource/Salary Slip?filters=[[\"employee\",\"=\",\"" + employeeId + "\"]]";
        
        HttpHeaders headers = getHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                Map.class
            );
            
            Map<String, Object> responseBody = response.getBody();
            return Arrays.asList(((List<Map>) responseBody.get("data"))
                .stream()
                .map(this::convertMapToSalarySlipDTO)
                .toArray(SalarySlipDTO[]::new));
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    @Override
    public SalarySlipDTO getEmployeeSalarySlipForPeriod(String employeeId, LocalDate startDate, LocalDate endDate) {
        String url = erpNextUrl + "/api/resource/Salary Slip?filters=[[\"employee\",\"=\",\"" + employeeId + "\"]," +
                "[\"start_date\",\"=\",\"" + startDate + "\"],[\"end_date\",\"=\",\"" + endDate + "\"]]";
        
        HttpHeaders headers = getHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                Map.class
            );
            
            Map<String, Object> responseBody = response.getBody();
            List<Map> data = (List<Map>) responseBody.get("data");
            
            if (data == null || data.isEmpty()) {
                throw new ResourceNotFoundException("No salary slip found for the specified period");
            }
            
            return convertMapToSalarySlipDTO(data.get(0));
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error retrieving salary slip for the specified period");
        }
    }

    @Override
    public SalarySlipDTO generateSalarySlip(String employeeId, LocalDate periodStartDate, LocalDate periodEndDate) {
        String url = erpNextUrl + "/api/resource/Salary Slip";
        
        HttpHeaders headers = getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> requestBody = Map.of(
            "employee", employeeId,
            "start_date", periodStartDate.toString(),
            "end_date", periodEndDate.toString()
        );
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<SalarySlipDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                SalarySlipDTO.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate salary slip: " + e.getMessage());
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, sessionManager.getSessionCookie());
        return headers;
    }

    private SalarySlipDTO convertMapToSalarySlipDTO(Map data) {
        SalarySlipDTO dto = new SalarySlipDTO();
        // Map the fields from the response to the DTO
        // Add appropriate mapping logic based on your API response structure
        return dto;
    }
}