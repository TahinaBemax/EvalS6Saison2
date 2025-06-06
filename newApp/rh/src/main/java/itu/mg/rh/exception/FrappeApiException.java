package itu.mg.rh.exception;

import itu.mg.rh.models.error.FrappeApiErrorResponse;
import lombok.Getter;

@Getter
public class FrappeApiException extends RuntimeException {

    private final FrappeApiErrorResponse errorResponse;

    public FrappeApiException(String message, FrappeApiErrorResponse errorResponse) {
        super(message);
        this.errorResponse = errorResponse;
    }
}
