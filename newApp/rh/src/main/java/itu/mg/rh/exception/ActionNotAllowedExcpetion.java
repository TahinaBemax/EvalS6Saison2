package itu.mg.rh.exception;

public class ActionNotAllowedExcpetion extends RuntimeException {
    public ActionNotAllowedExcpetion() {
        super("The price of an submited supplier quotation can't be updated");
    }
}
