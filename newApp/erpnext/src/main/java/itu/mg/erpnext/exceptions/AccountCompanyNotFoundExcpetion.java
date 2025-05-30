package itu.mg.erpnext.exceptions;

public class AccountCompanyNotFoundExcpetion extends Exception {
    public AccountCompanyNotFoundExcpetion(String company) {
        super(String.format("The company: %s don't have an account", company));
    }
}
