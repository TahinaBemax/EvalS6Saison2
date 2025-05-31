package itu.mg.rh.controller;

import itu.mg.rh.exception.FrappeApiException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
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
    public String exceptionHandler(HttpServletRequest request, RestClientException e) {
        logger.error(e.getLocalizedMessage());
        return handleByStatusCode(request);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> exceptionHandler(IOException e) {
        IOException ioException = new IOException("An error occured when reading the file: " + e.getMessage());
        return new ResponseEntity<>(ioException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public String exceptionHandler(HttpServletRequest request, Exception e) {
        logger.error(e.getLocalizedMessage());
        return handleByStatusCode(request);
    }

    @ExceptionHandler(FrappeApiException.class)
    public String exceptionHandler(HttpServletRequest request,FrappeApiException e) {
        logger.error(e.getLocalizedMessage());
        return handleByStatusCode(request);
    }

    private String handleByStatusCode(HttpServletRequest request){
        Object status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null){
            Integer statusCode = Integer.valueOf(status.toString());

            switch (statusCode) {
                case 404 -> {
                    return "error/404";
                }
                case 403 -> {
                    return "403";
                }
            }
        }
        return "error/500";
    }
}
