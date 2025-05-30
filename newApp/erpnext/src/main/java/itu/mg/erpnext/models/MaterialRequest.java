package itu.mg.erpnext.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class MaterialRequest {
    @NotNull
    String series = "MAT-MR-.YYYY.-";
    LocalDate required_by;
    @NotNull
    LocalDate transaction_date;
    @NotNull
    String material_request_type;
    @NotNull
    List<Map<String, Object>> items;
}
