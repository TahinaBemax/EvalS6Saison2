package itu.mg.erpnext.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemService extends MainService{
    public ItemService(RestTemplateBuilder builder, SessionManager sessionManager) {
        super(builder, sessionManager);
    }

    public boolean save(Item item)
    {
        String url = String.format("%s/api/resource/Item", this.getErpNextUrl());
        try {
            HttpHeaders headers = this.getHeaders();

            HttpEntity<Item> entity = new HttpEntity<>(item, headers);

            ResponseEntity<Map> response = this.getRestTemplate().exchange(url, HttpMethod.POST, entity, Map.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Item> getItems() throws JsonProcessingException {
        String[] fields = {"name", "item_name", "item_group"};

        String url = String.format("%s/api/resource/Item?fields=%s", this.getErpNextUrl(), new ObjectMapper().writeValueAsString(fields));

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
