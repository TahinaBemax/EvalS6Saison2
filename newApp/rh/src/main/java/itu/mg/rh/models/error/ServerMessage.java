package itu.mg.rh.models.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ServerMessage {
    String message;
    String title;
    String indicator;
    @JsonProperty("raise_exception")
    String raiseException;
    @JsonProperty("__frappe_exc_id")
    String frappeExcId;

}
