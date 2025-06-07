package itu.mg.rh.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.rh.csv.service.ImportCsv;
import itu.mg.rh.models.SalaryDetail;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SalarySlipServiceImpl implements SalarySlipService {

    private final MainService mainService;
    public static final Logger logger = LoggerFactory.getLogger(ImportCsv.class);
    private String[] fields;
    private String[] salaryDetailfields;

    @Autowired
    public SalarySlipServiceImpl(MainService mainService) {
        this.mainService = mainService;
        this.fields = new String[]{
                "name", "employee", "employee_name", "company",
                "department", "designation", "branch", "posting_date",
                "status", "currency", "payroll_frequency", "start_date", "end_date",
                "salary_structure", "mode_of_payment", "total_working_days", "absent_days",
                "payment_days", "gross_pay", "total_deduction", "total_earnings", "net_pay", "total_in_words"
        };

        this.salaryDetailfields = new String[]{
                "name", "salary_component", "abbr",
                "amount", "condition", "amount_based_on_formula",
                "formula", "parent", "parentfield", "parenttype"
        };
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
    public List<SalarySlip> getSalarySlipsByMonth(Integer month) throws JsonProcessingException {
        if (month == null || month > 12 || month < 1) {
            throw new RuntimeException("Month is null or invalid");
        }

        // Create start and end dates for the specified month
        int year = LocalDate.now().getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        String url = String.format("%s/api/resource/Salary Slip?filters=[[\"start_date\",\"<=\",\"%s\"],[\"end_date\",\">=\",\"%s\"]]&fields=%s",
                mainService.getErpNextUrl(),
                startDate,
                endDate,
                this.fieldsAsString());

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
            List<Map> salarySlipsData = (List<Map>) responseBody.get("data");

            return Arrays.asList(
                    salarySlipsData
                            .stream().map(this::convertMapToSalarySlip)
                            .toArray(SalarySlip[]::new)
            );
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
    public List<SalarySlip> findSalaryEmployeeDetails(Integer month, Integer year) throws JsonProcessingException {
        try {
            List<SalarySlip> salarySlips = new ArrayList<>();

            if (year != null)  {
                salarySlips = getSalarySlipsByDate(month, year);
            } else {
                salarySlips = ( month != null) ? getSalarySlipsByMonth(month) : getSalarySlips();
            }

            if (salarySlips == null)
                return salarySlips;

            return salarySlips
                    .stream()
                    .map(salarySlip -> {
                        List<SalaryDetail> details = this.getSalaryDetail(salarySlip.getName());

                        List<SalaryDetail> typeEarnings = getSalaryDetailEarningsType(details);
                        List<SalaryDetail> typeDeductions = getSalaryDetailDeductionsType(details);

                        salarySlip.setSalaryDetailDeductions(typeDeductions);
                        salarySlip.setSalaryDetailEarnings(typeEarnings);

                        return salarySlip;
                    })
                    .collect(Collectors.toList());
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    private List<SalaryDetail> getSalaryDetailEarningsType(List<SalaryDetail> salaryDetails) {
        return salaryDetails.stream().filter(detail -> detail.getParentField().equals("earnings")).toList();
    }

    private List<SalaryDetail> getSalaryDetailDeductionsType(List<SalaryDetail> salaryDetails) {
        return salaryDetails.stream().filter(detail -> detail.getParentField().equals("deductions")).toList();
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


    @Override
    public List<SalaryDetail> getSalaryDetail(String salarySlipName) {
        // Fetch salary details for this salary slip
        String url = String.format("%s/api/resource/Salary Slip/%s",
                mainService.getErpNextUrl(), salarySlipName
        );
        List<SalaryDetail> salaryDetails = new ArrayList<>();

        try {
            HttpHeaders headers = mainService.getHeaders();
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = mainService.getRestTemplate().exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            Map detailsData = (Map) responseBody.get("data");

            if (detailsData != null && !detailsData.isEmpty()) {
                List<Map> earnings = (List<Map>) detailsData.get("earnings");
                List<Map> deductions = (List<Map>) detailsData.get("deductions");

                List<SalaryDetail> salaryDetailEarnings = earnings.stream().map(this::convertMapToSalaryDetail).toList();
                List<SalaryDetail> salaryDetailDeductions = deductions.stream().map(this::convertMapToSalaryDetail).toList();

                salaryDetails.addAll(salaryDetailEarnings);
                salaryDetails.addAll(salaryDetailDeductions);

                return salaryDetails;
            }
        } catch (RestClientException e) {
            logger.error(e.getMessage());
            RestClientExceptionHandler.handleError(e);
        }

        return null;
    }


    private List<SalarySlip> getSalarySlipsByDate(Integer month, Integer year) throws JsonProcessingException {
        if (month == null || month > 12 || month < 1) {
            throw new RuntimeException("Month is null or invalid");
        }

        if (year == null || year <= 0) {
            throw new RuntimeException("Year is null or invalid");
        }
        LocalDate date = LocalDate.of(year, month, 1);

        String url = String.format("%s/api/resource/Salary Slip?filters=[[\"start_date\",\"<=\",\"%s\"],[\"end_date\",\">=\",\"%s\"]]&fields=%s",
                mainService.getErpNextUrl(),
                date,
                date,
                this.fieldsAsString());

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
            List<Map> salarySlipsData = (List<Map>) responseBody.get("data");

            return Arrays.asList(
                    salarySlipsData
                            .stream().map(this::convertMapToSalarySlip)
                            .toArray(SalarySlip[]::new)
            );
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }


    private SalaryDetail convertMapToSalaryDetail(Map data) {
        if (data == null || data.isEmpty())
            return null;

        SalaryDetail detail = new SalaryDetail();
        detail.setSalaryComponent((String) data.get("salary_component"));
        detail.setAbbr((String) data.get("abbr"));
        detail.setAmount((Double) data.get("amount"));
        detail.setCondition((String) data.get("condition"));
        detail.setFormula((String) data.get("formula"));
        detail.setAmountBasedOnFormula((Integer) data.get("amount_based_on_formula"));
        detail.setParent((String) data.get("parent"));
        detail.setParentType((String) data.get("parenttype"));
        detail.setParentField((String) data.get("parentfield"));
        return detail;
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
        dto.setTotalEarnings((Double) data.get("total_earnings"));
        dto.setNetPay((Double) data.get("net_pay"));
        dto.setTotalInWords((String) data.get("total_in_words"));
        return dto;
    }

    public String fieldsAsString() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this.fields);
    }

}