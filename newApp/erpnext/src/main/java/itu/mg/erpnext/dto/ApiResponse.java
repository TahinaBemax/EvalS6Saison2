package itu.mg.erpnext.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    List<T> data;
    String message;
    String status;
}
