package itu.mg.erpnext.controller;

import itu.mg.erpnext.exceptions.SupplierQuotationItemNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class ControllerAdvise {
    public static final Logger logger = LoggerFactory.getLogger(ControllerAdvise.class);

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> exceptionHandler(IOException e){
        IOException ioException = new IOException("Une erreur s'est produite lors de la lecture du fichier: " + e.getMessage());
        return new ResponseEntity<>(ioException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e){
        logger.error(e.getLocalizedMessage());
        return "error/500";
    }

    @ExceptionHandler(SupplierQuotationItemNotFoundException.class)
    public String exceptionHandler(SupplierQuotationItemNotFoundException e){
        logger.error(e.getLocalizedMessage());
        return "error/404";
    }
}
