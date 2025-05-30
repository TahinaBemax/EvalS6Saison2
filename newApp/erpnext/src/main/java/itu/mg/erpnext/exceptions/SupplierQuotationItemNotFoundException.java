package itu.mg.erpnext.exceptions;

public class SupplierQuotationItemNotFoundException extends RuntimeException{
    public SupplierQuotationItemNotFoundException(String name) {
        super(String.format("Supplier Quotation Item name=%s  is not found ", name));
    }

    public SupplierQuotationItemNotFoundException(String name, Throwable cause) {
        super(String.format("Supplier Quotation Item name=%s  is not found ", name), cause);
    }
}
