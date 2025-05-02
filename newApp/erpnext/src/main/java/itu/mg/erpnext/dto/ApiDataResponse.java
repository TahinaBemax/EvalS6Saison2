package itu.mg.erpnext.dto;

import itu.mg.erpnext.models.Supplier;
import lombok.Data;

import java.util.List;

@Data
public class ApiDataResponse<T> {
    List<T> data;
}
