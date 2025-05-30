package itu.mg.erprh.exception;

import itu.mg.erprh.models.error.FrappeApiErrorResponse;
import lombok.Getter;

@Getter
public class FrappeApiException extends RuntimeException {

    private final FrappeApiErrorResponse errorResponse;

    public FrappeApiException(String message, FrappeApiErrorResponse errorResponse) {
        super(message);
        this.errorResponse = errorResponse;
    }
}
