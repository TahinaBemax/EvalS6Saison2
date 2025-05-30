package itu.mg.erpnext.exceptions;

public class RequestForQuotationException extends RuntimeException{
    public RequestForQuotationException(String message) {
        super(message);
    }

    public RequestForQuotationException(String message, Throwable cause) {
        super(message, cause);
    }
}
