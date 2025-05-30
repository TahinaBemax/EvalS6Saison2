package itu.mg.erpnext.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestForQuotationSupplier {
    private String name;
    private String supplier;
    private String quote_status;
    private String supplier_name;
}
