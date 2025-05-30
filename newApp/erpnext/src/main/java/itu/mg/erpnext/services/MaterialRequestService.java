package itu.mg.erpnext.services;

import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.models.MaterialRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.Map;


@Service
public class MaterialRequestService extends MainService{
    public static final Logger logger = LoggerFactory.getLogger(MaterialRequestService.class);

    @Autowired
    public MaterialRequestService(RestTemplateBuilder restTemplate, SessionManager sessionManager) {
        super(restTemplate, sessionManager);
    }

    public boolean save(MaterialRequest materialRequest) {
        String resource = "Material Request";
        String url = String.format("%s/api/resource/%s", this.getErpNextUrl(), resource);

        try {
            HttpHeaders headers = this.getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<MaterialRequest> entity = new HttpEntity<>(materialRequest, headers);

            ResponseEntity<Map> responseEntity = this.getRestTemplate()
                    .exchange(url, HttpMethod.POST, entity, Map.class);

            return responseEntity.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }
}
