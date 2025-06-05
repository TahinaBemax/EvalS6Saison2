package itu.mg.rh.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.rh.models.Departement;
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

import java.util.List;
import java.util.Map;

@Service
public class DepartementServiceImpl {
    private final MainService mainService;
    public static final Logger logger = LoggerFactory.getLogger(DepartementServiceImpl.class);
    private String[] departmentFields;

    @Autowired
    public DepartementServiceImpl(MainService mainService) {
        this.mainService = mainService;
        departmentFields = new String[]{
            "name", "department_name", "parent_department", "company"
        };
    }

    public List<Departement> getAllDepartments() {
        StringBuilder urlBuilder = new StringBuilder(mainService.getErpNextUrl() + "/api/resource/Department");
        try {
            urlBuilder.append(String.format("?fields=%s", new ObjectMapper().writeValueAsString(departmentFields)));
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
                    .map(this::convertMapToDepartment)
                    .toList();
            }
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    private Departement convertMapToDepartment(Map data) {
        Departement department = new Departement();
        department.setName((String) data.get("name"));
        department.setDepartmentName((String) data.get("department_name"));
        department.setParentDepartment((String) data.get("parent_department"));
        department.setCompany((String) data.get("company"));
        return department;
    }
}