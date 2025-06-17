package itu.mg.rh.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.rh.csv.dto.export.SalaryStructureAssignmentExportDTO;
import itu.mg.rh.dto.SalaryDTO;
import itu.mg.rh.exception.FrappeApiException;
import itu.mg.rh.models.Employee;
import itu.mg.rh.models.SalaryStructure;
import itu.mg.rh.models.SalaryStructureAssignement;
import itu.mg.rh.services.EmployeeService;
import itu.mg.rh.services.MainService;
import itu.mg.rh.services.SalaryStructureAssignmentService;
import itu.mg.rh.services.SalaryStructureService;
import itu.mg.rh.services.helper.ApiDataPreparator;
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
public class SalaryStructureAssignmentServiceImpl implements SalaryStructureAssignmentService {
    private final MainService mainService;
    private String[] fields;
    private final EmployeeService employeeService;
    private final SalaryStructureService salaryStructureService;
    public static final Logger logger = LoggerFactory.getLogger(SalaryStructureAssignmentServiceImpl.class);

    @Autowired
    public SalaryStructureAssignmentServiceImpl( MainService mainService, EmployeeService employeeService, SalaryStructureService salaryStructureService) {
        this.mainService = mainService;
        this.employeeService = employeeService;
        this.salaryStructureService = salaryStructureService;
        this.fields = new String[]{
                "name", "employee", "employee_name", "company",
                "salary_structure", "from_date", "base", "payroll_payable_account", "currency", "variable", "docstatus"
        };
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

    @Override
    public boolean save(SalaryStructureAssignement salaryStructureAssignment) {
        try {
            // Prepare headers
            HttpHeaders headers = mainService.getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Make API call to Frappe
            HttpEntity<SalaryStructureAssignement> requestEntity = new HttpEntity<>(salaryStructureAssignment, headers);

            ResponseEntity<Map> response = this.mainService.getRestTemplate().exchange(
                    this.mainService.getErpNextUrl() + "/api/resource/Salary Structure Assignment",
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            return (response.getStatusCode() == HttpStatus.OK) ;
        } catch (RestClientException e) {
            RestClientExceptionHandler.handleError(e);
        }
        return false;
    }

    @Override
    public boolean saveAll(SalaryDTO salaryDTO) {
        List<SalaryStructureAssignement> salaries = prepareData(salaryDTO);

        try {
            for (SalaryStructureAssignement salary : salaries) {
                this.save(salary);
            }
        } catch (FrappeApiException e) {
            throw e;
        }

        return true;
    }

    @Override
    public boolean update(SalaryStructureAssignement body) {
        try {
            String name = body.getName();
            // Prepare headers
            HttpHeaders headers = mainService.getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Make API call to Frappe
            HttpEntity<SalaryStructureAssignement> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = this.mainService.getRestTemplate().exchange(
                    String.format("%s/api/resource/Salary Structure Assignment/%s", this.mainService.getErpNextUrl(), name),
                    HttpMethod.PUT,
                    requestEntity,
                    Map.class
            );

            return (response.getStatusCode() == HttpStatus.OK) ;
        } catch (RestClientException e) {
            RestClientExceptionHandler.handleError(e);
        }
        return false;
    }

    @Override
    public boolean delete(String name) {
        try {
            // Prepare headers
            HttpHeaders headers = mainService.getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Make API call to Frappe
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = this.mainService.getRestTemplate().exchange(
                    String.format("%s/api/resource/Salary Structure Assignment/%s", this.mainService.getErpNextUrl(), name),
                    HttpMethod.DELETE,
                    requestEntity,
                    Map.class
            );

            return (response.getStatusCode() == HttpStatus.OK) ;
        } catch (RestClientException e) {
            RestClientExceptionHandler.handleError(e);
        }
        return false;
    }

    @Override
    public boolean cancel(String name) {
        try {
            // Prepare headers
            HttpHeaders headers = mainService.getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Make API call to Frappe
            Map<String, Object> body = new HashMap<>();
            body.put("docstatus", 2);
            HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = this.mainService.getRestTemplate().exchange(
                    String.format("%s/api/resource/Salary Structure Assignment/%s", this.mainService.getErpNextUrl(), name),
                    HttpMethod.PUT,
                    requestEntity,
                    Map.class
            );

            return (response.getStatusCode() == HttpStatus.OK) ;
        } catch (RestClientException e) {
            RestClientExceptionHandler.handleError(e);
        }
        return false;
    }

    @Override
    public List<SalaryStructureAssignement> findAllByEmployeeID(String employee) {
        String url = String.format("%s/api/resource/Salary Structure Assignment?filter=[\"employee\", \"LIKE\", %s]&fields=%s",
                mainService.getErpNextUrl(), employee, this.fieldsAsString());

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
            return ((List<Map>) data.get("data")).stream().map(this::mapToSalaryStructureAssignment).toList();
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    @Override
    public List<SalaryStructureAssignement> findAll() {
        String url = String.format("%s/api/resource/Salary Structure Assignment?fields=%s", mainService.getErpNextUrl(), this.fieldsAsString());

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
            return ((List<Map>) data.get("data")).stream().map(this::mapToSalaryStructureAssignment).toList();
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }


    private List<SalaryStructureAssignement> prepareData(SalaryDTO salaryDTO){
        List<String> employeeName = salaryDTO.getEmployeeName();
        List<Employee> employees = employeeService.findAll();
        List<SalaryStructureAssignement> salaryStructureAssignements = new ArrayList<>();

        if (employeeName == null || employeeName.isEmpty())
            throw new RuntimeException("Employee is null!");

        for (String employee : employeeName) {
            List<Employee> collect = employees.stream().filter(e -> e.getEmployeID().equals(employee)).toList();
            if (collect.isEmpty()){
                 throw new RuntimeException(String.format("Employee %s not found", employee));
            }

            double variable = 0;
            int start = salaryDTO.getStartDate().getMonthValue();
            int end = salaryDTO.getEndDate().getMonthValue();

            Employee matchedEmployee = collect.get(0);
            List<SalaryStructureAssignement> existingSalAssignments = this.findAllByEmployeeID(employee);
            SalaryStructureAssignement lastEmployeeSalaryAssignement = this.getEmployeeLastSalaryAssignment(existingSalAssignments);

            double base = (salaryDTO.getBase() == 0 && lastEmployeeSalaryAssignement != null ) ? lastEmployeeSalaryAssignement.getBase() : salaryDTO.getBase();
            String salaryStructure = getSalaryStructureName(lastEmployeeSalaryAssignement);

            for (int i = start; i <= end ; i++) {
                int year = salaryDTO.getStartDate().getYear();
                LocalDate fromDate = LocalDate.of(year, i, 01);

                if (hasSalaryStructureAssignment(fromDate, existingSalAssignments)){
                    continue;
                }

                SalaryStructureAssignement salary = new SalaryStructureAssignement();

                salary.setEmployee(matchedEmployee.getEmployeID());
                salary.setEmployee_name(matchedEmployee.getFullName());
                salary.setCompany(matchedEmployee.getCompany());
                salary.setSalary_structure(salaryStructure);
                salary.setFrom_date(fromDate);
                salary.setPayroll_payable_account(lastEmployeeSalaryAssignement.getPayroll_payable_account());
                salary.setCurrency("EUR");
                salary.setBase(base);
                salary.setVariable(variable);
                salary.setDocstatus(1);

                salaryStructureAssignements.add(salary);
            }

        }

        return salaryStructureAssignements;
    }

    public String fieldsAsString() {
        try {
            return new ObjectMapper().writeValueAsString(this.fields);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private SalaryStructureAssignement getEmployeeLastSalaryAssignment(List<SalaryStructureAssignement> existingSalAssignments){
        LocalDate last = null;
        SalaryStructureAssignement matched = null;
        for (SalaryStructureAssignement existingSalAssignment : existingSalAssignments) {
            LocalDate from_date = existingSalAssignment.getFrom_date();
            if (last == null || from_date.isAfter(last)){
                last = from_date;
                matched = existingSalAssignment;
            }
        }

        return matched;
    }

    private boolean hasSalaryStructureAssignment(LocalDate date, List<SalaryStructureAssignement> existingSalAssignments){
        for (SalaryStructureAssignement existingSalAssignment : existingSalAssignments) {
            LocalDate from_date = existingSalAssignment.getFrom_date();
            if (from_date.isEqual(date)){
                return true;
            }
        }
        return false;
    }

    private String getSalaryStructureName(SalaryStructureAssignement lastEmployeeSalaryAssignement){
        if(lastEmployeeSalaryAssignement != null) {
            return lastEmployeeSalaryAssignement.getSalary_structure();
        }
        List<SalaryStructure> allActiveSalaryStructure = salaryStructureService.findAllActiveSalaryStructure();

        SalaryStructure salaryStructure = allActiveSalaryStructure.stream().findFirst().orElseThrow();
        return salaryStructure.getName();
    }

    private SalaryStructureAssignement mapToSalaryStructureAssignment(Map data){
        if(data == null || data.isEmpty())
            return null;

        SalaryStructureAssignement structureAssignement = new SalaryStructureAssignement();

        structureAssignement.setName((String) data.get("name"));
        structureAssignement.setEmployee((String) data.get("employee"));
        structureAssignement.setEmployee_name((String) data.get("employee_name"));
        structureAssignement.setCompany((String) data.get("company"));
        structureAssignement.setSalary_structure((String) data.get("salary_structure"));
        structureAssignement.setFrom_date((LocalDate.parse(data.get("from_date").toString())));
        structureAssignement.setPayroll_payable_account((String) data.get("payroll_payable_account"));
        structureAssignement.setCurrency((String) data.get("currency"));
        structureAssignement.setBase((Double) data.get("base"));
        structureAssignement.setVariable((Double) data.get("variable"));
        structureAssignement.setDocstatus((Integer) data.get("docstatus"));

        return structureAssignement;
    }
}