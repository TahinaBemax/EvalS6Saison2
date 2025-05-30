package itu.mg.erprh.csv.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class SupplierQuotation {
    @CsvBindByName(column = "ref_request_quotation", required = true)
    int refRequestQuotation;

    @CsvBindByName(column = "supplier", required = true)
    String supplier;
}
