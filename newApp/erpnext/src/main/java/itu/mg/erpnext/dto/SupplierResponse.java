package itu.mg.erpnext.dto;

import itu.mg.erpnext.models.Supplier;
import lombok.Data;

import java.util.List;

@Data
public class SupplierResponse {
    List<Supplier> data;
}
