package itu.mg.rh.services;

import itu.mg.rh.components.SessionManager;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Data
public class LoginService {
    private final RestTemplate restTemplate;
    @Value("${erpnext.url}")
    private String erpNextUrl;
    private final SessionManager sessionManager;

    public LoginService(RestTemplateBuilder builder, SessionManager sessionManager) {
        this.restTemplate = builder.build();
        this.sessionManager = sessionManager;
    }

    public boolean login(String username, String password) {
        String url = this.erpNextUrl + "/api/method/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("usr", username);
        body.put("pwd", password);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            // Récupérer le cookie de session
            if (response.getStatusCode().is2xxSuccessful()){
                List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
                if (!cookies.isEmpty()) {
                    sessionManager.setSessionCookie(cookies.get(0)); // JSESSIONID ou sid
                }

                return true;
            }
        } catch (RestClientException e){
            throw new UsernameNotFoundException("Bad credentials");
        }

        return false;
    }
}
