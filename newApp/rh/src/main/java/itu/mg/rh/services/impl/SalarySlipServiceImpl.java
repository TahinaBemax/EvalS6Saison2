package itu.mg.rh.services.impl;

import itu.mg.rh.csv.service.ImportCsv;
import itu.mg.rh.dto.SalarySlipDTO;
import itu.mg.rh.services.MainService;
import itu.mg.rh.services.SalarySlipService;
import itu.mg.rh.utils.RestClientExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class SalarySlipServiceImpl implements SalarySlipService {
    
    private final MainService mainService;
    public static final Logger logger = LoggerFactory.getLogger(ImportCsv.class);

    @Autowired
    public SalarySlipServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }




    @Override
    public SalarySlipDTO getSalarySlipById(String salarySlipId) {
        String url = mainService.getErpNextUrl() + "/api/resource/Salary Slip/" + salarySlipId;
        
        HttpHeaders headers = mainService.getHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<SalarySlipDTO> response = mainService.getRestTemplate().exchange(
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
        String url = mainService.getErpNextUrl() + "/api/resource/Salary Slip?filters=[[\"employee\",\"=\",\"" + employeeId + "\"]]";
        
        HttpHeaders headers = mainService.getHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = mainService.getRestTemplate().exchange(
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
        String url = mainService.getErpNextUrl() + "/api/resource/Salary Slip?filters=[[\"employee\",\"=\",\"" + employeeId + "\"]," +
                "[\"start_date\",\"=\",\"" + startDate + "\"],[\"end_date\",\"=\",\"" + endDate + "\"]]";
        
        HttpHeaders headers = mainService.getHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = mainService.getRestTemplate().exchange(
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
        String url = mainService.getErpNextUrl() + "/api/resource/Salary Slip";
        
        HttpHeaders headers = mainService.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> requestBody = Map.of(
            "employee", employeeId,
            "start_date", periodStartDate.toString(),
            "end_date", periodEndDate.toString()
        );
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<SalarySlipDTO> response = mainService.getRestTemplate().exchange(
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


    private SalarySlipDTO convertMapToSalarySlipDTO(Map data) {
        SalarySlipDTO dto = new SalarySlipDTO();
        // Map the fields from the response to the DTO
        // Add appropriate mapping logic based on your API response structure
        return dto;
    }
}