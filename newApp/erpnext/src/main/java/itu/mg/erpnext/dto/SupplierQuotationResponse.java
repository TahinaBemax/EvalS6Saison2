package itu.mg.erpnext.dto;

import itu.mg.erpnext.models.SupplierQuotation;
import lombok.Data;

import java.util.List;

@Data
public class SupplierQuotationResponse {
    List<SupplierQuotation> data;
}
