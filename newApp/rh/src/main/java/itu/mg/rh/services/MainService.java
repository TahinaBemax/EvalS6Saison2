package itu.mg.rh.services;


import itu.mg.rh.components.SessionManager;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Data
public class MainService {
    @Value("${erpnext.url}")
    private String erpNextUrl;
    private final RestTemplate restTemplate;
    private final SessionManager sessionManager;

    public MainService(RestTemplateBuilder restTemplate, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.restTemplate = restTemplate.build();
    }

    public HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, this.sessionManager.getSessionCookie()); // Utilise le cookie de session
        return  headers;
    }
}
