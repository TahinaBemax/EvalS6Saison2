package itu.mg.erpnext.dto;

import itu.mg.erpnext.models.Warehouse;
import lombok.Data;

import java.util.List;

@Data
public class WarehouseResponse {
    private List<Warehouse> data;
}

