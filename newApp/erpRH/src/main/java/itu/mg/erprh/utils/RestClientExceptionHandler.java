package itu.mg.erprh.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import itu.mg.erprh.exception.FrappeApiException;
import itu.mg.erprh.models.error.FrappeApiErrorResponse;
import org.springframework.web.client.RestClientException;

public class RestClientExceptionHandler {

    public static void handleError(RestClientException e) {
        FrappeApiErrorResponse error = null;
        try {
            error = FrappeApiErrorResponse.getInstance(e);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        throw new FrappeApiException("Frappe API Error: " + error.getExceptionType(), error);
    }
}
