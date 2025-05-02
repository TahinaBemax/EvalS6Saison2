package itu.mg.erpnext.exceptions;

public class ActionNotAllowedExcpetion extends RuntimeException {
    public ActionNotAllowedExcpetion() {
        super("The price of an submited supplier quotation can't be updated");
    }
}
