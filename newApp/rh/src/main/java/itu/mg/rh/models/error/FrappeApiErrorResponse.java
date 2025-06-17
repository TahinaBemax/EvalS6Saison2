package itu.mg.rh.models.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

@Data
public class FrappeApiErrorResponse {
    private String exception;

    @JsonProperty("exc_type")
    private String exceptionType;

    @JsonProperty("_exc_source")
    private String exceptionSource;

    private String exc;

    @JsonProperty("_server_messages")
    private String rawServerMessages;

    // Méthode utilitaire pour parser les objets ServerMessage
    public List<ServerMessage> getServerMessages() {
        ObjectMapper mapper = new ObjectMapper();
        List<ServerMessage> parsedMessages = new ArrayList<>();
        if (rawServerMessages != null) {
                try {
                    List<String> innerJsonStrings = mapper.readValue(rawServerMessages, new TypeReference<List<String>>() {});
                    parsedMessages = innerJsonStrings.stream()
                            .map(str -> {
                                try {
                                    return mapper.readValue(str, ServerMessage.class);
                                } catch (Exception e) {
                                    throw new RuntimeException("Error parsing ServerMessage JSON: " + str, e);
                                }
                            })
                            .toList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return parsedMessages;
    }
    public static FrappeApiErrorResponse getInstance(RestClientException e) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        String rawMessage = e.getMessage();

        // Extraire la partie JSON entre le premier et dernier guillemet double
        int firstBrace = rawMessage.indexOf("{");
        int lastBrace = rawMessage.lastIndexOf("}");

        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            String jsonPart = rawMessage.substring(firstBrace, lastBrace + 1);
            try {
                return objectMapper.readValue(jsonPart, FrappeApiErrorResponse.class);
            } catch (Exception parseEx) {
                throw new RuntimeException("Failed to parse extracted Frappe JSON error: " + jsonPart, parseEx);
            }
        } else {
            throw new RuntimeException("Failed to locate JSON part in: " + rawMessage);
        }
    }

    public String serverMessageToString(){
        StringBuilder messages = new StringBuilder();
        for (ServerMessage serverMessage : getServerMessages()) {
            messages.append(serverMessage.getMessage()).append("\\r\\n");
        }

        return messages.toString();
    }
}
