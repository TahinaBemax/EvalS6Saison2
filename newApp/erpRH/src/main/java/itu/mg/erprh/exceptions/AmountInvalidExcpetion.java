package itu.mg.erprh.exceptions;

public class AmountInvalidExcpetion extends RuntimeException {
    public AmountInvalidExcpetion(double newPrice) {
        super(String.format("Amount %f invalid", newPrice));
    }
}
