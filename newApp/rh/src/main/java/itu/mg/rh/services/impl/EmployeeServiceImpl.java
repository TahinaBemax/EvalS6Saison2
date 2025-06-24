package itu.mg.rh.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.rh.csv.service.impl.ImportCsvImpl;
import itu.mg.rh.models.Employee;
import itu.mg.rh.services.EmployeeService;
import itu.mg.rh.services.MainService;
import itu.mg.rh.utils.RestClientExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final MainService mainService;
    public static final Logger logger = LoggerFactory.getLogger(ImportCsvImpl.class);
    private String[] employeeFields;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public EmployeeServiceImpl(MainService mainService) {
        this.mainService = mainService;
        employeeFields = new String[]{
                "employee_name", "gender", "date_of_birth",
                "date_of_joining", "status", "company",
                "department", "designation", "reports_to",
                "employment_type", "grade", "name"
        };
    }

    @Override
    public List<Map<String, Object>> getAll() {
        String sql = "SELECT employee_name FROM tabEmployee";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Employee> getEmployee(String fullName, String company) {
        StringBuilder urlBuilder = new StringBuilder(mainService.getErpNextUrl() + "/api/resource/Employee");
        try {
            urlBuilder.append(String.format("?fields=%s", new ObjectMapper().writeValueAsString(employeeFields)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (fullName != null || company != null) {
            urlBuilder.append("&filters=[");
            if (fullName != null) {
                urlBuilder.append("[\"employee_name\",\"like\",\"").append(fullName).append("\"]");
            }
            if (company != null) {
                if (fullName != null) {
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
            if ( responseBody.containsKey("data")) {
                List<Map> data = (List<Map>) responseBody.get("data");
                return data.stream()
                    .map(this::convertMapToEmployee)
                    .toList();
            }
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }
    @Override
    public List<Employee> findAll() {
        StringBuilder urlBuilder = new StringBuilder(mainService.getErpNextUrl() + "/api/resource/Employee");
        try {
            urlBuilder.append(String.format("?fields=%s", new ObjectMapper().writeValueAsString(employeeFields)));
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
            if ( responseBody.containsKey("data")) {
                List<Map> data = (List<Map>) responseBody.get("data");
                return data.stream()
                        .map(this::convertMapToEmployee)
                        .toList();
            }
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }
    @Override
    public boolean save(Employee employee) {
        if (employee == null)
            throw new RuntimeException("Employee is null");

        String url = String.format("%s/api/resource/Employee", this.mainService.getErpNextUrl());
        HttpHeaders headers = mainService.getHeaders();
        Map<String, Object> body = this.convertEmployeeToMap(employee);

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
    public boolean delete(String name) {
        if (name == null)
            throw new RuntimeException("Employee is null");

        String url = String.format("%s/api/resource/Employee/%s", this.mainService.getErpNextUrl(), name);
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
    public Employee findById(String name) {
        if (name == null || name.isBlank())
            throw new RuntimeException("Employee Name is null or Empty");

        String url = String.format("%s/api/resource/Employee?filters=[\"employee_name\", \"LIKE\", %s]&fields=%s",
                mainService.getErpNextUrl() , name, this.fieldAsString());

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
            if ( responseBody.containsKey("data")) {
                List<Map> data = (List<Map>) responseBody.get("data");

                return data.stream()
                        .map(this::convertMapToEmployee)
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
    public boolean update(Employee employee) {
        if (employee == null)
            throw new RuntimeException("Employee is null");
        String name = employee.getEmployeID();
        String url = String.format("%s/api/resource/Employee/%s", this.mainService.getErpNextUrl(), name);

        HttpHeaders headers = mainService.getHeaders();
        Map<String, Object> body = this.convertEmployeeToMap(employee);

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


    private Employee convertMapToEmployee(Map data) {
        Employee employee = new Employee();
        employee.setFullName((String) data.get("employee_name"));
        employee.setEmployeID((String) data.get("name"));
        employee.setStatus((String) data.get("status"));
        employee.setCompany((String) data.get("company"));
        employee.setGender((String) data.get("gender"));
        employee.setDateOfBirth(LocalDate.parse((String) data.get("date_of_birth")));
        employee.setDepartment((String) data.get("department"));
        employee.setDesignation((String) data.get("designation"));
        employee.setEmploymentType((String) data.get("employment_type"));
        employee.setGrade((String) data.get("grade"));
        employee.setReportsTo((String) data.get("reports_to"));
        if (data.get("date_of_joining") != null) {
            employee.setDateOfJoining(LocalDate.parse((String) data.get("date_of_joining")));
        }
        return employee;
    }
    private Map<String, Object> convertEmployeeToMap(Employee data) {
        Map<String, Object> employee = new HashMap();

        employee.put("employee_name", data.getFullName());
        employee.put("first_name", data.getFirstName());
        employee.put("last_name", data.getLastName());
        employee.put("company", data.getCompany());
        employee.put("gender", data.getGender());
        employee.put("date_of_birth", data.getDateOfBirth());
        employee.put("date_of_joining", data.getDateOfJoining());
        employee.put("department", data.getDepartment());
        employee.put("employment_type", data.getEmploymentType());
        employee.put("grade", data.getGrade());
        employee.put("reports_to", data.getReportsTo());
        employee.put("naming_series", "HR-EMP-");
        employee.put("status", "Active");
        employee.put("designation", data.getDesignation());
        employee.put("branch", data.getBranch());
        employee.put("holiday_list", data.getHolidayList());
        employee.put("salary_currency", "EUR");
        return employee;
    }
    private String fieldAsString() {
        try {
            return new ObjectMapper().writeValueAsString(employeeFields);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
