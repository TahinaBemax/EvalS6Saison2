package itu.mg.erpnext.services;

import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.ItemResponse;
import itu.mg.erpnext.models.Item;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class ItemService extends MainService{
    public ItemService(RestTemplateBuilder builder, SessionManager sessionManager) {
        super(builder, sessionManager);
    }

    public List<Item> getItems() {
        String url = this.getErpNextUrl() + "/api/resource/Item?fields=[\"name\", \"item_name\"]";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ItemResponse> response = null;
        try {
            response = this.getRestTemplate().exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ItemResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()){
                return response.getBody().getData();
            }
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
