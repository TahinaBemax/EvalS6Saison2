package itu.mg.erpnext.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.erpnext.components.SessionManager;
import itu.mg.erpnext.dto.ItemGroupResponse;
import itu.mg.erpnext.dto.ItemResponse;
import itu.mg.erpnext.models.Item;
import itu.mg.erpnext.models.ItemGroup;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class ItemGroupService extends MainService{
    public ItemGroupService(RestTemplateBuilder builder, SessionManager sessionManager) {
        super(builder, sessionManager);
    }

    public List<ItemGroup> getItemGroups() throws JsonProcessingException {
        String[] fields = {"name", "item_group_name"};

        String url = String.format("%s/api/resource/Item Group?fields=%s", this.getErpNextUrl(), new ObjectMapper().writeValueAsString(fields));

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, this.getSessionManager().getSessionCookie());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ItemGroupResponse> response = null;
        try {
            response = this.getRestTemplate().exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ItemGroupResponse.class
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
