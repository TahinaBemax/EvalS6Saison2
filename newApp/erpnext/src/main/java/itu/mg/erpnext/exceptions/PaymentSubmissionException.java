package itu.mg.erpnext.exceptions;

import org.springframework.web.client.RestClientException;

public class PaymentSubmissionException extends RuntimeException {
    public PaymentSubmissionException(String s) {
    }

    public PaymentSubmissionException(String errorSubmittingPayment, RestClientException e) {
    }

    public PaymentSubmissionException(String unexpectedErrorSubmittingPayment, Exception e) {
    }
}
