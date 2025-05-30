package itu.mg.erpnext.dto;

import itu.mg.erpnext.models.RequestForQuotation;
import itu.mg.erpnext.models.RequestForQuotationSupplier;
import lombok.Data;

import java.util.List;

@Data
public class RequestForQuotationSupplierResponse {
    List<RequestForQuotationSupplier> data;
}
