package itu.mg.rh.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.rh.csv.dto.export.SalaryStructureExportDTO;
import itu.mg.rh.services.MainService;
import itu.mg.rh.services.SalaryStructureService;
import itu.mg.rh.services.helper.ApiDataPreparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class SalaryStructureServiceImpl implements SalaryStructureService {
    private final ObjectMapper objectMapper;
    private final MainService mainService;

    public SalaryStructureServiceImpl(ObjectMapper objectMapper, MainService mainService) {
        this.objectMapper = objectMapper;
        this.mainService = mainService;
    }

    @Override
    public boolean insertSalaryStructures(List<SalaryStructureExportDTO> salaryStructures) {
        try {
            // Prepare headers
            HttpHeaders headers = mainService.getHeaders();

            // Create request body
            List<Map<String, Object>> data = ApiDataPreparator.salaryStructure(salaryStructures);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("salary_structures", data);

            // Make API call to Frappe
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = this.mainService.getRestTemplate().exchange(
                    this.mainService.getErpNextUrl() + "/api/method/importapp.api.import.insert_salary_structures",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            return (response.getStatusCode() == HttpStatus.OK) ;
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
    }
} 