package itu.mg.erpnext.services;

import itu.mg.erpnext.components.SessionManager;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Data
public class MainService {
    @Value("${erpnext.url}")
    private String erpNextUrl;
    private final RestTemplate restTemplate;
    private final SessionManager sessionManager;

    @Autowired
    public MainService(RestTemplateBuilder builder, SessionManager sessionManager) {
        this.restTemplate = builder.build();
        this.sessionManager = sessionManager;
    }
}
