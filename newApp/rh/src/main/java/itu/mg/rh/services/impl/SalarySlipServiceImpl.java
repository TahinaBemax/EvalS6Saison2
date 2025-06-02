package itu.mg.rh.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.rh.csv.service.ImportCsv;
import itu.mg.rh.models.SalarySlip;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class SalarySlipServiceImpl implements SalarySlipService {
    
    private final MainService mainService;
    public static final Logger logger = LoggerFactory.getLogger(ImportCsv.class);
    private String[] fields ;

    @Autowired
    public SalarySlipServiceImpl(MainService mainService) {
        this.mainService = mainService;
        this.fields = new String[] {
                "name", "employee", "employee_name", "company",
                "department", "designation", "branch", "posting_date",
                "status", "currency", "payroll_frequency", "start_date", "end_date",
                "salary_structure", "mode_of_payment", "total_working_days", "absent_days",
                "payment_days","gross_pay", "total_deduction", "net_pay", "total_in_words"
        };
    }

    public String fieldsAsString() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this.fields);
    }

    @Override
    public SalarySlip getSalarySlipById(String salarySlipId) {
        String url = mainService.getErpNextUrl() + "/api/resource/Salary Slip/" + salarySlipId;
        
        HttpHeaders headers = mainService.getHeaders();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<SalarySlip> response = mainService.getRestTemplate().exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                SalarySlip.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Salary slip not found with ID: " + salarySlipId);
        }
    }

    @Override
    public List<SalarySlip> getEmployeeSalarySlips(String employeeId) throws JsonProcessingException {
        String url = String.format("%s/api/resource/Salary Slip?filters=[[\"employee\",\"=\",\"%s\"]]&fields=%s",
                mainService.getErpNextUrl(), employeeId, this.fieldsAsString());
        
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
                .map(this::convertMapToSalarySlip)
                .toArray(SalarySlip[]::new));
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    @Override
    public List<SalarySlip> getSalarySlips() throws JsonProcessingException {
        String url = String.format("%s/api/resource/Salary Slip?fields=%s", mainService.getErpNextUrl(), this.fieldsAsString());

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
                    .map(this::convertMapToSalarySlip)
                    .toArray(SalarySlip[]::new));
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    @Override
    public SalarySlip getEmployeeSalarySlipForPeriod(String employeeId, LocalDate startDate, LocalDate endDate) {
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
            
            return convertMapToSalarySlip(data.get(0));
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error retrieving salary slip for the specified period");
        }
    }

    @Override
    public SalarySlip generateSalarySlip(String employeeId, LocalDate periodStartDate, LocalDate periodEndDate) {
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
            ResponseEntity<SalarySlip> response = mainService.getRestTemplate().exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                SalarySlip.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate salary slip: " + e.getMessage());
        }
    }
    @Override
    public byte[] exportSalarySlipToPdf(String salarySlipId) {
        String url = String.format("%s/api/method/frappe.utils.print_format.download_pdf", mainService.getErpNextUrl());
        
        HttpHeaders headers = mainService.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> requestBody = Map.of(
            "doctype", "Salary Slip",
            "name", salarySlipId,
            "format", "Standard"
        );
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<byte[]> response = mainService.getRestTemplate().exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                byte[].class
            );
            
            return response.getBody();
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    private SalarySlip convertMapToSalarySlip(Map data) {
        SalarySlip dto = new SalarySlip();
        dto.setName((String) data.get("name"));
        dto.setEmployeeId((String) data.get("employee"));
        dto.setEmployeeName((String) data.get("employee_name"));
        dto.setCompany((String) data.get("company"));
        dto.setDepartment((String) data.get("department"));
        dto.setDesignation((String) data.get("designation"));
        dto.setBranch((String) data.get("branch"));
        dto.setPaymentDate(LocalDate.parse(data.get("posting_date").toString()));
        dto.setStatus((String) data.get("status"));
        dto.setCurrency((String) data.get("currency"));
        dto.setPayrollFrequency((String) data.get("payroll_frequency"));
        dto.setStartDate(LocalDate.parse(data.get("start_date").toString()));
        dto.setEndDate(LocalDate.parse(data.get("end_date").toString()));
        dto.setSalaryStructure((String) data.get("salary_structure"));
        dto.setModeOfPayment((String) data.get("mode_of_payment"));
        dto.setTotalWorkingDays((Double) data.get("total_working_days"));
        dto.setAbsentDays((Double) data.get("absent_days"));
        dto.setPaymentDays((Double) data.get("payment_days"));
        dto.setGrossPay((Double) data.get("gross_pay"));
        dto.setTotalDeduction((Double) data.get("total_deduction"));
        dto.setNetPay((Double) data.get("net_pay"));
        dto.setTotalInWords((String) data.get("total_in_words"));
        return dto;
    }
}