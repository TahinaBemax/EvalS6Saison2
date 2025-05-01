package itu.mg.erpnext.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestForQuotation {
    private String name;
    private String docstatus;
    private String company;
    private LocalDate transaction_date;
    private LocalDate schedule_date;
    private String messageForSupplier;
    private String supplier;
    private String supplier_name;
    private String quote_status;
}
