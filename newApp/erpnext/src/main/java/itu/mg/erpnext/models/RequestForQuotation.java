package itu.mg.erpnext.models;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RequestForQuotation {
    private String name;
    private String docstatus;
    private String company;
    private LocalDate transaction_date;
    private LocalDate schedule_date;
    List<RequestForQuotationSupplier> suppliers;
    List<RequestForQuotationItem> items;
}
