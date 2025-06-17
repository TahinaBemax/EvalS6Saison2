package itu.mg.rh.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.rh.models.PayrollEntry;
import itu.mg.rh.services.MainService;
import itu.mg.rh.services.PayrollEntryService;
import itu.mg.rh.utils.RestClientExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PayrollEntryServiceImpl implements PayrollEntryService {
    private final MainService mainService;
    private String[] payrollEntryFields;

    @Autowired
    public PayrollEntryServiceImpl(MainService mainService) {
        this.mainService = mainService;
        payrollEntryFields = new String[]{
            "name", "company", "posting_date", "payroll_frequency",
            "start_date", "end_date", "status", "payment_account",
            "mode_of_payment", "salary_slip_based_on_timesheet",
            "payroll_payable_account", "currency", "docstatus"
        };
    }

    @Override
    public List<PayrollEntry> getPayrollEntry(String name, String company) {
        StringBuilder urlBuilder = new StringBuilder(mainService.getErpNextUrl() + "/api/resource/Payroll Entry");
        try {
            urlBuilder.append(String.format("?fields=%s", new ObjectMapper().writeValueAsString(payrollEntryFields)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (name != null || company != null) {
            urlBuilder.append("&filters=[");
            if (name != null) {
                urlBuilder.append("[\"name\",\"like\",\"").append(name).append("\"]");
            }
            if (company != null) {
                if (name != null) {
                    urlBuilder.append(",");
                }
                urlBuilder.append("[\"company\",\"=\",\"").append(company).append("\"]");
            }
            urlBuilder.append("]");
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
                    .map(this::convertMapToPayrollEntry)
                    .toList();
            }
        } catch (RestClientException e) {
            log.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    @Override
    public List<PayrollEntry> findAll() {
        StringBuilder urlBuilder = new StringBuilder(mainService.getErpNextUrl() + "/api/resource/Payroll Entry");
        try {
            urlBuilder.append(String.format("?fields=%s", new ObjectMapper().writeValueAsString(payrollEntryFields)));
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
                    .map(this::convertMapToPayrollEntry)
                    .toList();
            }
        } catch (RestClientException e) {
            log.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    @Override
    public boolean save(PayrollEntry payrollEntry) {
        if (payrollEntry == null)
            throw new RuntimeException("Payroll Entry is null");

        String url = String.format("%s/api/resource/Payroll Entry", this.mainService.getErpNextUrl());
        HttpHeaders headers = mainService.getHeaders();
        Map<String, Object> body = this.convertPayrollEntryToMap(payrollEntry);

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
    public boolean delete(String name) {
        if (name == null)
            throw new RuntimeException("Payroll Entry name is null");

        String url = String.format("%s/api/resource/Payroll Entry/%s", this.mainService.getErpNextUrl(), name);
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
    public PayrollEntry findById(String name) {
        if (name == null || name.isBlank())
            throw new RuntimeException("Payroll Entry Name is null or Empty");

        String url = String.format("%s/api/resource/Payroll Entry?filters=[\"name\", \"LIKE\", %s]&fields=%s",
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
                    .map(this::convertMapToPayrollEntry)
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
    public boolean update(PayrollEntry payrollEntry) {
        if (payrollEntry == null)
            throw new RuntimeException("Payroll Entry is null");
        
        String name = payrollEntry.getName();
        String url = String.format("%s/api/resource/Payroll Entry/%s", this.mainService.getErpNextUrl(), name);

        HttpHeaders headers = mainService.getHeaders();
        Map<String, Object> body = this.convertPayrollEntryToMap(payrollEntry);

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

    private PayrollEntry convertMapToPayrollEntry(Map data) {
        PayrollEntry entry = new PayrollEntry();
        entry.setName((String) data.get("name"));
        entry.setCompany((String) data.get("company"));
        entry.setPostingDate(LocalDate.parse(data.get("posting_date").toString()));
        entry.setPayrollFrequency((String) data.get("payroll_frequency"));
        entry.setStartDate((LocalDate.parse(data.get("start_date").toString())));
        entry.setEndDate((LocalDate.parse(data.get("end_date").toString())));
        entry.setStatus((String) data.get("status"));
        entry.setPaymentAccount((String) data.get("payment_account"));
        entry.setModeOfPayment((String) data.get("mode_of_payment"));
        entry.setSalarySlipBasedOnTimesheet((Integer) data.get("salary_slip_based_on_timesheet"));
        entry.setPayrollPayableAccount((String) data.get("payroll_payable_account"));
        entry.setCurrency((String) data.get("currency"));
        entry.setDocstatus((Integer) data.get("docstatus"));
        return entry;
    }

    private Map<String, Object> convertPayrollEntryToMap(PayrollEntry data) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("name", data.getName());
        entry.put("company", data.getCompany());
        entry.put("posting_date", data.getPostingDate());
        entry.put("payroll_frequency", data.getPayrollFrequency());
        entry.put("start_date", data.getStartDate());
        entry.put("end_date", data.getEndDate());
        entry.put("status", data.getStatus());
        entry.put("payment_account", data.getPaymentAccount());
        entry.put("mode_of_payment", data.getModeOfPayment());
        entry.put("salary_slip_based_on_timesheet", data.getSalarySlipBasedOnTimesheet());
        entry.put("payroll_payable_account", data.getPayrollPayableAccount());
        entry.put("currency", data.getCurrency());
        entry.put("docstatus", data.getDocstatus());
        return entry;
    }

    private String fieldAsString() {
        try {
            return new ObjectMapper().writeValueAsString(payrollEntryFields);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}