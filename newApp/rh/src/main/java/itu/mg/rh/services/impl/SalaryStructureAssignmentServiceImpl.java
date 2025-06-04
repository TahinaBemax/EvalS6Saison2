package itu.mg.rh.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.rh.csv.dto.export.SalaryStructureAssignmentExportDTO;
import itu.mg.rh.csv.dto.export.SalaryStructureExportDTO;
import itu.mg.rh.services.MainService;
import itu.mg.rh.services.SalaryStructureAssignmentService;
import itu.mg.rh.services.SalaryStructureService;
import itu.mg.rh.services.helper.ApiDataPreparator;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalaryStructureAssignmentServiceImpl implements SalaryStructureAssignmentService {
    private final ObjectMapper objectMapper;
    private final MainService mainService;

    public SalaryStructureAssignmentServiceImpl(ObjectMapper objectMapper, MainService mainService) {
        this.objectMapper = objectMapper;
        this.mainService = mainService;
    }


    @Override
    public boolean saveSalaryStructureAssignment(List<SalaryStructureAssignmentExportDTO> salaryStructureAssignments) {
        try {
            // Prepare headers
            HttpHeaders headers = mainService.getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create request body
            List<Map<String, Object>> data = ApiDataPreparator.salaryStructureAssignmentsData(salaryStructureAssignments);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("assignments", data);

            // Make API call to Frappe
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = this.mainService.getRestTemplate().exchange(
                    this.mainService.getErpNextUrl() + "/api/method/importapp.api.import.insert_salary_structure_assignments",
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            return (response.getStatusCode() == HttpStatus.OK) ;
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
    }
}