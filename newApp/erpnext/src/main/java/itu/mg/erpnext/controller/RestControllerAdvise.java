package itu.mg.erpnext.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import itu.mg.erpnext.dto.ApiResponse;
import itu.mg.erpnext.exceptions.SupplierQuotationItemNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class RestControllerAdvise {
    public static final Logger logger = LoggerFactory.getLogger(RestControllerAdvise.class);

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<?> exceptionHandler(JsonProcessingException e){
        logger.error(e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(null, "Internal Server Error", "error"));
    }
}
