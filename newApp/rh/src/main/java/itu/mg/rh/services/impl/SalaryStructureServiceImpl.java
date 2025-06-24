package itu.mg.rh.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.rh.csv.dto.export.SalaryStructureExportDTO;
import itu.mg.rh.dto.SalaryStructureFormDto;
import itu.mg.rh.models.SalaryStructure;
import itu.mg.rh.models.SalaryStructureAssignement;
import itu.mg.rh.services.MainService;
import itu.mg.rh.services.SalaryStructureService;
import itu.mg.rh.services.helper.ApiDataPreparator;
import itu.mg.rh.utils.RestClientExceptionHandler;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class SalaryStructureServiceImpl implements SalaryStructureService {
    private final MainService mainService;
    private String[] fields;

    public SalaryStructureServiceImpl(ObjectMapper objectMapper, MainService mainService) {
        this.mainService = mainService;
        this.fields = new String[] {
                "name", "is_active", "company", "total_earning", "total_deduction",
                "net_pay", "letter_head", "is_default", "currency", "payroll_frequency", "salary_component", "mode_of_payment",
                "payment_account", "salary_slip_base_on_timesheet", "docstatus"};
    }

    @Override
    public SalaryStructure findById(String name) {
        if (name == null || name.isBlank())
            throw new RuntimeException("Salary Structure Name is null or Empty");

        String url = String.format("%s/api/resource/Salary Structure?filters=[\"name\", \"LIKE\", %s]&fields=%s",
            mainService.getErpNextUrl(), name, this.fieldsAsString());

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
            if (responseBody.containsKey("data")) {
                List<Map> data = (List<Map>) responseBody.get("data");
                return data.stream()
                    .map(this::mapToSalaryStructure)
                    .findFirst()
                    .orElseThrow();
            }
        } catch (RestClientException e) {
            log.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    @Override
    public boolean delete(String name) {
        if (name == null)
            throw new RuntimeException("Salary Structure name is null");

        String url = String.format("%s/api/resource/Salary Structure/%s", this.mainService.getErpNextUrl(), name);
        HttpHeaders headers = mainService.getHeaders();

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map> response = mainService.getRestTemplate().exchange(
                url,
                HttpMethod.DELETE,
                requestEntity,
                Map.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            log.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return false;
    }

    @Override
    public boolean update(SalaryStructure salaryStructure) {
        if (salaryStructure == null)
            throw new RuntimeException("Salary Structure is null");
        
        String name = salaryStructure.getName();
        String url = String.format("%s/api/resource/Salary Structure/%s", this.mainService.getErpNextUrl(), name);

        HttpHeaders headers = mainService.getHeaders();
        Map<String, Object> body = this.convertSalaryStructureToMap(salaryStructure);

        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = mainService.getRestTemplate().exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Map.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            log.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return false;
    }

    @Override
    public boolean save(SalaryStructure salaryStructure) {
        if (salaryStructure == null)
            throw new RuntimeException("Salary Structure is null");

        String url = String.format("%s/api/resource/Salary Structure", this.mainService.getErpNextUrl());
        HttpHeaders headers = mainService.getHeaders();
        Map<String, Object> body = this.convertSalaryStructureToMap(salaryStructure);

        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = mainService.getRestTemplate().exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Map.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            log.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return false;
    }

    @Override
    public boolean save(SalaryStructureFormDto salaryStructure) {
        return false;
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

    @Override
    public List<SalaryStructure> findAllActiveSalaryStructure() {
        String url = String.format("%s/api/resource/Salary Structure?fields=%s", mainService.getErpNextUrl(), this.fieldsAsString());

        HttpHeaders headers = mainService.getHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = mainService.getRestTemplate().exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );

            Map data = (Map) response.getBody();

            return ((List<Map<String, Object>>) data.get("data"))
                            .stream()
                            .map(this::mapToSalaryStructure)
                            .toList();
        } catch (RestClientException e) {
            log.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    @Override
    public List<SalaryStructure> getSalaryStructure(String name, String company) {
        return null;
    }

    @Override
    public List<SalaryStructure> findAll() {
        return null;
    }

    private SalaryStructure mapToSalaryStructure(Map<String, Object> data) {
        SalaryStructure temp = new SalaryStructure();
        temp.setName((String) data.get("name"));
        temp.setCompany((String) data.get("company"));
        temp.setIsActive((String) data.get("is_active"));
        temp.setDocstatus((Integer) data.get("docstatus"));
        temp.setSalaryComponent((String) data.get("salary_component"));
        temp.setTotalEarning((Double) data.get("total_earning"));
        temp.setTotalDeduction((Double) data.get("total_deduction"));
        temp.setNetPay((Double) data.get("net_pay"));
        temp.setLetterHead((String) data.get("letter_head"));
        temp.setIsDefault((Integer) data.get("is_default"));
        temp.setCurrency((String) data.get("currency"));
        temp.setPayrollFrequency((String) data.get("payroll_frequency"));
        temp.setModeOfPayment((String) data.get("mode_of_payment"));
        temp.setPaymentAccount((String) data.get("payment_account"));
        temp.setSalarySlipBasedOnTimesheet((Integer) data.get("salary_slip_base_on_timesheet"));
        return temp;
    }

    private String fieldsAsString() {
        try {
            return new ObjectMapper().writeValueAsString(this.fields);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> convertSalaryStructureToMap(SalaryStructure data) {
        Map<String, Object> structure = new HashMap<>();
        structure.put("name", data.getName());
        structure.put("company", data.getCompany());
        structure.put("is_active", data.getIsActive());
        structure.put("docstatus", data.getDocstatus());
        structure.put("salary_component", data.getSalaryComponent());
        structure.put("total_earning", data.getTotalEarning());
        structure.put("total_deduction", data.getTotalDeduction());
        structure.put("net_pay", data.getNetPay());
        structure.put("letter_head", data.getLetterHead());
        structure.put("is_default", data.getIsDefault());
        structure.put("currency", data.getCurrency());
        structure.put("payroll_frequency", data.getPayrollFrequency());
        structure.put("mode_of_payment", data.getModeOfPayment());
        structure.put("payment_account", data.getPaymentAccount());
        structure.put("salary_slip_base_on_timesheet", data.getSalarySlipBasedOnTimesheet());
        return structure;
    }
} 