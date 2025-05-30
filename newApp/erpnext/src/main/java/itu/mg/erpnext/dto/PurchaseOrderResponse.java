package itu.mg.erpnext.dto;

import itu.mg.erpnext.models.PurchaseOrder;
import itu.mg.erpnext.models.SupplierQuotation;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrderResponse {
    List<PurchaseOrder> data;
}
