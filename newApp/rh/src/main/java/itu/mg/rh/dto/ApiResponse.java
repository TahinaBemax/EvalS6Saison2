package itu.mg.rh.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.mg.rh.models.error.FrappeApiErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    List<T> data;
    String message;
    String status;
    List<T> errors;
    //LocalDateTime at;

/*    public ApiResponse(List<T> data, String message, String status, List<T> errors) {
        this.data = data;
        this.message = message;
        this.status = status;
        this.errors = errors;
        //this.at = LocalDateTime.now();
    }*/

    public static ApiResponse parseJsonErrorToApiResponse(RestClientException e) {
        final ObjectMapper objectMapper = new ObjectMapper();
        String rawMessage = e.getMessage();

        // Extraire la partie JSON entre le premier et dernier guillemet double
        int firstBrace = rawMessage.indexOf("{");
        int lastBrace = rawMessage.lastIndexOf("}");

        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            String jsonPart = rawMessage.substring(firstBrace, lastBrace + 1);
            try {
                return objectMapper.readValue(jsonPart, ApiResponse.class);
            } catch (Exception parseEx) {
                throw new RuntimeException("Failed to parse extracted Frappe JSON error: " + jsonPart, parseEx);
            }
        } else {
            throw new RuntimeException("Failed to locate JSON part in: " + rawMessage);
        }
    }
}
