package itu.mg.erpnext.csv.exception;

public class DuplicateValueException extends RuntimeException {

    public DuplicateValueException(String fieldName, String value) {
        super(String.format("Field: %s, The value %s is already exist!", fieldName, value));
    }
}
