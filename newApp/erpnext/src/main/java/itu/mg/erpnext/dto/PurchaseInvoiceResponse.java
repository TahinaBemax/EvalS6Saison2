package itu.mg.erpnext.dto;

import itu.mg.erpnext.models.PurchaseInvoice;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseInvoiceResponse {
    List<PurchaseInvoice> data;
}
