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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final MainService mainService;
    public static final Logger logger = LoggerFactory.getLogger(ImportCsvImpl.class);
    private String[] employeeFields;

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
        employee.setEmployementType((String) data.get("employment_type"));
        employee.setGrade((String) data.get("grade"));
        employee.setReportsTo((String) data.get("reports_to"));
        if (data.get("date_of_joining") != null) {
            employee.setDateOfJoining(LocalDate.parse((String) data.get("date_of_joining")));
        }
        return employee;
    }
}
