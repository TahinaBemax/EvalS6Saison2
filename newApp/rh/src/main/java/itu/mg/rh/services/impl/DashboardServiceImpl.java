package itu.mg.rh.services.impl;

import itu.mg.rh.dto.TotalSalarySlipPerMonth;
import itu.mg.rh.services.DashboardService;
import itu.mg.rh.services.MainService;
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
public class DashboardServiceImpl implements DashboardService {
    private final MainService mainService;
    private final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

    @Autowired
    public DashboardServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    public List<TotalSalarySlipPerMonth> findTotalSalarySlipPerMonth(Integer year) {
        if (year == null || year > LocalDate.now().getYear()) {
            throw new RuntimeException("Year is invalid");
        }

        String url = String.format("%s/api/method/importapp.api.dashboard.total_salary_per_month",
                mainService.getErpNextUrl());

        HttpHeaders headers = mainService.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("year", year);
        HttpEntity<Map> requestEntity = new HttpEntity<>(body, headers);

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
                            .stream().map(this::convertMapToTotalSalarySlipPerMonth)
                            .toArray(TotalSalarySlipPerMonth[]::new)
            );
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }
        return null;
    }

    private TotalSalarySlipPerMonth convertMapToTotalSalarySlipPerMonth(Map map) {
        TotalSalarySlipPerMonth temp = new TotalSalarySlipPerMonth();
        temp.setMonth((Integer) map.get("month"));
        temp.setYear((Integer) map.get("year"));
        temp.setTotalAmount((Double) map.get("total_salary"));
        Object details = map.get("details");
        if (details != null) {
            temp.setDetails((List<Map<String, Object>>) details);
        } else {
            temp.setDetails(new ArrayList<>());
        }

        return temp;
    }
}
