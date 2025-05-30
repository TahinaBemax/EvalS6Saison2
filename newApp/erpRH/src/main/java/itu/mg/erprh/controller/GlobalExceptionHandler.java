package itu.mg.erprh.controller;

import itu.mg.erprh.exception.FrappeApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {
    public static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RestClientException.class)
    public String exceptionHandler(RestClientException e) {
        logger.error(e.getLocalizedMessage());
        return "error/500";
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> exceptionHandler(IOException e) {
        IOException ioException = new IOException("An error occured when reading the file: " + e.getMessage());
        return new ResponseEntity<>(ioException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e) {
        logger.error(e.getLocalizedMessage());
        return "error/500";
    }

    @ExceptionHandler(FrappeApiException.class)
    public String exceptionHandler(FrappeApiException e) {
        logger.error(e.getLocalizedMessage());
        return "error/500";
    }

}
