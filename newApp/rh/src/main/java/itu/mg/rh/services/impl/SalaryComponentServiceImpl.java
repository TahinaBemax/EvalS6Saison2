package itu.mg.rh.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.rh.models.SalaryComponent;
import itu.mg.rh.services.MainService;
import itu.mg.rh.services.SalaryComponentService;
import itu.mg.rh.utils.RestClientExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalaryComponentServiceImpl implements SalaryComponentService {
    private final MainService mainService;
    public static final Logger logger = LoggerFactory.getLogger(SalaryComponentServiceImpl.class);
    private String[] salaryComponentFields;

    @Autowired
    public SalaryComponentServiceImpl(MainService mainService) {
        this.mainService = mainService;
        salaryComponentFields = new String[]{
            "name", "type", "description", "is_tax_applicable", "salary_component",
                "salary_component_abbr", "is_flexible_benefit", "statistical_component",
                "depends_on_payment_days", "do_not_include_in_total", "disabled", "condition",
                "description", "amount", "amount_based_on_formula", "formula",
        };
    }

    @Override
    public List<SalaryComponent> findAll() {
        StringBuilder urlBuilder = new StringBuilder(mainService.getErpNextUrl() + "/api/resource/Salary Component");
        try {
            urlBuilder.append(String.format("?fields=%s", new ObjectMapper().writeValueAsString(salaryComponentFields)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String url = urlBuilder.toString();
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
                    .map(this::convertMapToSalaryComponent)
                    .toList();
            }
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    @Override
    public SalaryComponent findById(String name) {
        if (name == null || name.isBlank())
            throw new RuntimeException("Salary Component Name is null or Empty");

        String url = String.format("%s/api/resource/Salary Component?filters=[\"name\", \"LIKE\", %s]&fields=%s",
            mainService.getErpNextUrl(), name, this.fieldAsString());

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
                    .map(this::convertMapToSalaryComponent)
                    .findFirst()
                    .orElseThrow();
            }
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    @Override
    public boolean delete(String name) {
        if (name == null)
            throw new RuntimeException("Salary Component name is null");

        String url = String.format("%s/api/resource/Salary Component/%s", this.mainService.getErpNextUrl(), name);
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
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return false;
    }

    @Override
    public boolean update(SalaryComponent salaryComponent) {
        if (salaryComponent == null)
            throw new RuntimeException("Salary Component is null");
        
        String name = salaryComponent.getName();
        String url = String.format("%s/api/resource/Salary Component/%s", this.mainService.getErpNextUrl(), name);

        HttpHeaders headers = mainService.getHeaders();
        Map<String, Object> body = this.convertSalaryComponentToMap(salaryComponent);

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
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return false;
    }

    @Override
    public boolean save(SalaryComponent salaryComponent) {
        if (salaryComponent == null)
            throw new RuntimeException("Salary Component is null");

        String url = String.format("%s/api/resource/Salary Component", this.mainService.getErpNextUrl());
        HttpHeaders headers = mainService.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = this.convertSalaryComponentToMap(salaryComponent);

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
            logger.error(e.getLocalizedMessage());
            throw RestClientExceptionHandler.handleError(e);
        }
    }

    private SalaryComponent convertMapToSalaryComponent(Map data) {
        SalaryComponent component = new SalaryComponent();
        component.setName((String) data.get("name"));
        component.setType((String) data.get("type"));
        component.setDescription((String) data.get("description"));
        component.setCondition((String) data.get("condition"));
        component.setSalaryComponent((String) data.get("salary_component"));
        component.setSalaryComponentAbbr((String) data.get("salary_component_abbr"));
        component.setDependsOnPaymentDays((Integer) data.get("depends_on_payment_days"));
        component.setIsTaxApplicable((Integer) data.get("is_tax_applicable"));
        component.setIsFlexibleBenefit((Integer) data.get("is_flexible_benefit"));
        component.setStatisticalComponent((Integer) data.get("statistical_component"));
        component.setDoNotIncludeInTotal((Integer) data.get("do_not_include_in_total"));
        component.setDisabled((Integer) data.get("disabled"));
        component.setAmountBasedOnFormula((Integer) data.get("amount_based_on_formula"));
        component.setFormula((String) data.get("formula"));
        component.setAmount((Double) data.get("amount"));
        return component;
    }

    private Map<String, Object> convertSalaryComponentToMap(SalaryComponent data) {
        Map<String, Object> component = new HashMap<>();
        component.put("name", data.getName());
        component.put("salary_component", data.getName());
        component.put("salary_component_abbr", data.getSalaryComponentAbbr());
        component.put("type", data.getType());
        component.put("description", data.getDescription());
        component.put("condition", data.getCondition());
        component.put("depends_on_payment_days", data.getDependsOnPaymentDays());
        component.put("is_tax_applicable", data.getIsTaxApplicable());
        component.put("deduct_full_tax_on_selected_payroll_date", data.getDeductFullTaxOnSelectedPayrollDate());
        component.put("variable_based_on_taxable_salary", data.getVariableBasedOnTaxableSalary());
        component.put("is_income_tax_component", data.getIsIncomeTaxComponent());
        component.put("exempted_from_income_tax", data.getExemptedFromIncomeTax());
        component.put("round_to_the_nearest_integer", data.getRoundToTheNearestInteger());
        component.put("statistical_component", data.getStatisticalComponent());
        component.put("do_not_include_in_total", data.getDoNotIncludeInTotal());
        component.put("remove_if_zero_valued", data.getRemoveIfZeroValued());
        component.put("disabled", data.getDisabled());
        component.put("amount", data.getAmount());
        component.put("amount_based_on_formula", data.getAmountBasedOnFormula());
        component.put("formula", data.getFormula());
        component.put("is_flexible_benefit", data.getIsFlexibleBenefit());
        return component;
    }

    private String fieldAsString() {
        try {
            return new ObjectMapper().writeValueAsString(salaryComponentFields);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
